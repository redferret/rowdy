package rowdy;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Stack;
import static rowdy.Rowdy.*;

/**
 * Constructs a parse tree with the given language and then allows you to
 * execute the parse tree.
 *
 * @author Richard DeSilvey
 */
public class RowdyRunner {

  private Node root;
  /**
   * Stores the name of each identifier or function
   */
  private final HashMap<String, Value> globalSymbolTable;
  /**
   * Keeps track of the level of loops the program is in.
   */
  private final Stack<Node> activeLoops;
  /**
   * Keeps track of the functions currently being called.
   */
  private final Stack<Function> callStack;
  /**
   * Reference to the main function
  */
  private Node main;
  
  private boolean runAsSingleLine;
  
  private List<Value> programParamValues;

  public RowdyRunner() {
    this.root = null;
    main = null;
    callStack = new Stack<>();
    activeLoops = new Stack<>();
    globalSymbolTable = new HashMap<>();
  }
  
  public void initialize(RowdyBuilder builder, boolean runAsSingleLine) {
    this.root = builder.getProgram();
    if (runAsSingleLine) {
      callStack.clear();
      activeLoops.clear();
      globalSymbolTable.clear();
    }
    this.runAsSingleLine = runAsSingleLine;
  }
  
  public void initialize(RowdyBuilder builder) {
    this.initialize(builder, true);
  }
  
  /**
   * Only call this method if the program has stopped executing.
   */
  public void dumpCallStack() {
    if (!callStack.isEmpty()){
      System.out.print("Call Stack:\n");
      while(!callStack.isEmpty()){
        Function function = callStack.pop();
        System.out.println("->" + function.getName() + ": line " + 
                function.getLineCalledOn());
      }
    }
  }

  public void execute() throws Exception {
    this.execute(new ArrayList<>());
  }
  
  /**
   * Runs the program loaded into the parse tree.
   *
   * @param programParams The program parameters
   * @throws java.lang.Exception
   */
  public void execute(List<Value> programParams) throws Exception {
    // Declare global variables
    declareGlobals(root);
    this.programParamValues = programParams;
    if (runAsSingleLine && main == null) {
      throw new RuntimeException("main method not found");
    } else if (runAsSingleLine && main != null){
      executeStmt(main, null);
    } else {
      executeStmt(root, null);
    }
    
  }

  /**
   * Scans the program looking for global variables and function declarations.
   * Each global is allocated in the main symbol table and functions are also
   * placed in the main symbol table.
   *
   * @param parent
   * @param programParamValues
   */
  public void declareGlobals(Node parent) {
    Node currentTreeNode;
    ArrayList<Node> children = parent.getAll();
    int currentID;
    Value rightValue;
    
    setAsGlobal("true", new Value(true));
    setAsGlobal("false", new Value(false));
    
    for (int i = 0; i < children.size(); i++) {
      currentTreeNode = children.get(i);
      currentID = currentTreeNode.symbol().id();
      switch (currentID) {
        case ASSIGN_STMT:
          rightValue = getValue(currentTreeNode.get(EXPRESSION));
          setAsGlobal((Terminal) currentTreeNode.get(ID).symbol(), rightValue);
          break;
        case FUNCTION:
          String functionName = ((Terminal) currentTreeNode.get(ID).symbol()).getName();
          if (!functionName.equals("main")) {
            setAsGlobal(functionName, new Value(currentTreeNode));
          } else {
            if (globalSymbolTable.get(functionName) != null) {
              throw new RuntimeException("main method already defined");
            }
              setAsGlobal(functionName, new Value(currentTreeNode));
              main = parent;
              }
          break;
        default:
          declareGlobals(currentTreeNode);
      }
    }
  }

  /**
   * Executes program statements
   *
   * @param parent The start of execution
   * @param seqControl The node that flags sequence control to stop executing on
   * the parent. This lets break and return statements drop the sequence and not
   * execute any remaining statements until sequence control is given back to
   * the original caller.
   */
  public void executeStmt(Node parent, Node seqControl) {
    Node currentTreeNode;
    if (parent == null) 
      throw new IllegalArgumentException("parent node is null");
    ArrayList<Node> children = parent.getAll();
    Value rightValue;
    for (int i = 0, curID; i < children.size(); i++) {
      currentTreeNode = children.get(i);
      curID = currentTreeNode.symbol().id();
      switch (curID) {
        case FUNCTION:
          Value exitValue = executeFunc(currentTreeNode);
          if (exitValue == null){
            exitValue = new Value(0);
          }
          System.exit(exitValue.valueToDouble().intValue());
        case ASSIGN_STMT:
          Terminal idTerminal = (Terminal) currentTreeNode.get(ID).symbol();
          rightValue = getValue(currentTreeNode.get(EXPRESSION));
          String idNameToAssign = idTerminal.getName();
          if (idNameToAssign.equals("true") || idNameToAssign.equals("false")) {
            throw new RuntimeException("Can't assign new value to constant '" + 
                    idNameToAssign + "' on line " + currentTreeNode.getLine());
          }
          allocate(idTerminal, rightValue);
          break;
        case IF_STMT:
          Node ifExpr = currentTreeNode.get(EXPRESSION);
          Value ifExprValue = getValue(ifExpr);
          if (ifExprValue.valueToBoolean()) {
            Node ifStmtList = currentTreeNode.get(STMT_BLOCK).get(STMT_LIST);
            executeStmt(ifStmtList, seqControl);
          } else {
            executeStmt(currentTreeNode.get(ELSE_PART), seqControl);
          }
          break;
        case LOOP_STMT:
          String idName = ((Terminal) currentTreeNode.get(ID).symbol()).getName();
          Function curFunction = callStack.peek();
          Value curValue = curFunction.getValue(idName);
          if (curValue == null) {
            curFunction.allocate(idName, new Value(0));
            activeLoops.push(currentTreeNode);
            currentTreeNode.setSeqActive(true);
          } else {
            throw new RuntimeException("ID '" + idName + "' already in use "+
                    "on line " + currentTreeNode.getLine());
          }
          boolean done = false;
          Node loopStmtList = currentTreeNode.get(STMT_BLOCK).get(STMT_LIST);
          while (!done) {
            executeStmt(loopStmtList, currentTreeNode);
            done = !currentTreeNode.isSeqActive();
          }
          curFunction.unset(idName);
          break;
        case BREAK_STMT:
          if (!currentTreeNode.get(ID_OPTION).hasChildren()) {
            if (activeLoops.isEmpty()) {
              throw new RuntimeException("No loop to break. Line " 
                      + currentTreeNode.getLine());
            }
            Node idOption = activeLoops.peek();
            idName = ((Terminal) idOption.get(ID).symbol()).getName();
          } else {
            idName = ((Terminal) currentTreeNode.get(ID_OPTION).get(ID).symbol()).getName();
          }
          curFunction = callStack.peek();
          if (curFunction.getValue(idName) == null) {
            throw new RuntimeException("The ID '" + idName + "' doesn't exist."
                    + " Line " + currentTreeNode.getLine());
          }
          for (;;) {
            Node lp = activeLoops.pop();
            lp.setSeqActive(false);
            String tempBinding = ((Terminal) lp.get(ID).symbol()).getName();
            curFunction.unset(tempBinding);
            if (idName.equals(tempBinding)) {
              break;
            }
          }
          break;
        case READ_STMT:
          Scanner keys = new Scanner(System.in);
          String inValue;
          Node firstID = currentTreeNode.get(ID);
          Terminal t = (Terminal) executeExpr(firstID, null).getValue();
          allocate(t, new Value(keys.nextLine()));
          if (currentTreeNode.hasChildren()) {
            Node paramsTail = currentTreeNode.get(PARAMS_TAIL);
            while (paramsTail.hasChildren()) {
              currentTreeNode = paramsTail.get(ID);
              Value v = executeExpr(currentTreeNode, null);
              inValue = keys.nextLine();
              allocate((Terminal) v.getValue(), new Value(inValue));
              paramsTail = paramsTail.get(PARAMS_TAIL);
            }
          }
          break;
        case FUNC_CALL:
          String funcName = ((Terminal) currentTreeNode.get(ID).symbol()).getName();
          if (funcName.equals("main")) {
            throw new RuntimeException("Can't recurse on main. Line " 
                    + currentTreeNode.getLine());
          }
          executeFunc(currentTreeNode);
          break;
        case RETURN_STMT:
          Function functionReturning = callStack.peek();
          seqControl.setSeqActive(false);
          Value toSet = getValue(currentTreeNode.get(EXPRESSION));
          functionReturning.setReturnValue(toSet);
          break;
        case PRINT_STMT:
          StringBuilder printValue = new StringBuilder();
          printValue.append(getValue(currentTreeNode.get(EXPRESSION)).valueToString());
          Node atomTailNode = currentTreeNode.get(EXPR_LIST);
          while (atomTailNode.hasChildren()) {
            printValue.append(getValue(atomTailNode.get(EXPRESSION)).valueToString());
            atomTailNode = atomTailNode.get(EXPR_LIST);
          }
          char c;
          StringBuilder toPrint = new StringBuilder();
          if (printValue.toString().contains("\\n")){
            for (int l = 0; l < printValue.length(); l++) {
              c = printValue.charAt(l);
              if ((c == '\\') && (printValue.charAt(++l) == 'n')) {
                System.out.println(toPrint);
                toPrint = new StringBuilder();
              } else {
                toPrint.append(c);
              }
            }
            System.out.print(toPrint);
          } else {
            System.out.print(printValue);
          }
          break;
        default:
          if (seqControl != null) {
            if (seqControl.isSeqActive()) {
              executeStmt(currentTreeNode, seqControl);
            }
          } else {
            executeStmt(currentTreeNode, null);
          }
      }
    }
  }

  /**
   * When a function is called, and not allocated, the method will collect the
   * actual parameters being passed into the function (if any) and then allocate
   * them in the function's symbol table, then the function's stmt-list is
   * executed. If a return is called sequence control drops through the
   * function's stmt-list and returns the function's return value here, null is
   * always returned if no value is explicitly returned or if no value is
   * returned but the return stmt is still called.
   *
   * @param cur The function being called
   * @return The function's return value
   */
  public Value executeFunc(Node cur) {
    // 1. Collect parameters
    Value funcVal = null;
    String funcName = ((Terminal) cur.get(ID).symbol()).getName();
    if (!callStack.isEmpty()){
      funcVal = callStack.peek().getValue(funcName);
    } 
    if (funcVal == null) {
      if (globalSymbolTable.get(funcName) == null) {
        throw new RuntimeException("Function '" + funcName + "' not defined on "
                + "line " + cur.getLine());
      } else {
        funcVal = globalSymbolTable.get(funcName);
      }
    }
    List<Value> parameterValues = new ArrayList<>();
    Node expr = cur.get(EXPRESSION, false);// This might be 'main'
    if (expr != null && expr.hasChildren()) {
      parameterValues.add(getValue(cur.get(EXPRESSION)));
      Node atomTailNode = cur.get(EXPR_LIST);
      while (atomTailNode.hasChildren()) {
        parameterValues.add(getValue(atomTailNode.get(EXPRESSION)));
        atomTailNode = atomTailNode.get(EXPR_LIST);
      }
    } else if (funcName.equals("main")){
      parameterValues = programParamValues;
    }
    
    Node functionNode = (Node) funcVal.getValue();
    List<String> paramsList = new ArrayList<>();
    if (!parameterValues.isEmpty()) {
      Node functionBody = functionNode.get(FUNCTION_BODY);
      Node paramsNode = functionBody.get(PARAMETERS);
      if (paramsNode.hasChildren()) {
        Value paramValue = executeExpr(paramsNode, null);

        paramsList.add(((Terminal) paramValue.getValue()).getName());
        Node paramsTailNode = paramsNode.get(PARAMS_TAIL);
        while (paramsTailNode.hasChildren()) {
          paramsList.add(((Terminal) executeExpr(paramsTailNode.get(ID), null).getValue()).getName());
          paramsTailNode = paramsTailNode.get(PARAMS_TAIL);
        }
      }
    }
    // 2. Copy actual parameters to formal parameters
    HashMap<String, Value> params = new HashMap<>();
    String paramName;
    for (int p = 0; p < paramsList.size(); p++) {
      paramName = paramsList.get(p);
      params.put(paramName, parameterValues.get(p));
    }
    // 3. Push the function onto the call stack
    Function function = new Function(funcName, params, cur.getLine());
    callStack.push(function);
    // 4. Get and execute the stmt-list
    Node funcStmtBlock = functionNode.get(FUNCTION_BODY).get(STMT_BLOCK);
    Node stmtList = funcStmtBlock.get(STMT_LIST), seqControl = new Node(null, 0);
    seqControl.setSeqActive(true);
    executeStmt(stmtList, seqControl);
    // When finished, remove the function from the
    // call stack and free it's memory then return
    // it's value.
    function = callStack.pop();
    function.free();
    return function.getReturnValue();
  }

  /**
   * If the variable is not a global variable it
   * will allocate the variable to the current function if it doesn't exist.
   * If the variable exists the value passed in will overwrite the current value.
   *
   * @param idTerminal The variable to allocate or change
   * @param value The Value being allocated or changed
   */
  public void allocate(Terminal idTerminal, Value value) {
    Value exists = globalSymbolTable.get(idTerminal.getName());
    if (exists != null) {
      setAsGlobal(idTerminal, value);
    } else {
      try {
        Function currentFunction = callStack.peek();
        currentFunction.allocate(idTerminal, value);
      } catch (EmptyStackException e) {}
    }
  }

  /**
   * allocates/sets memory using the name of the symbol and a given value
   *
   * @param idName The variable being set
   * @param value The value of the variable
   */
  public void setAsGlobal(String idName, Value value) {
    Value curValue;
    curValue = globalSymbolTable.get(idName);
    if (curValue == null) {
      globalSymbolTable.put(idName, value);
    } else {
      globalSymbolTable.remove(idName);
      globalSymbolTable.put(idName, value);
    }
  }

  public void setAsGlobal(Terminal cur, Value value) {
    setAsGlobal(cur.getName(), value);
  }

  public boolean isset(Node cur) {
    Value o = (Value) executeExpr(cur, null);
    return isset(o);
  }

  public boolean isset(Value value) {
    if (value == null) {
      return false;
    }
    String v = ((Terminal) value.getValue()).getName();
    Value val = globalSymbolTable.get(v);
    if (val == null) {
      Function currentFunction = callStack.peek();
      return (currentFunction.getValue(value) != null);
    }
    return true;
  }

  /**
   * Performs the same task as getValue(Value) but uses a tree node to access
   * the atom.
   *
   * @param cur The tree node
   * @return A value object with an atom stored in it.
   */
  public Value getValue(Node cur) {
    Value value = (Value) executeExpr(cur, null);
    return fetch(value, cur);
  }
  
  public Value fetch(Value value, Node curSeq) {
    if (value == null) {
      return null;
    }
    if (value.getValue() instanceof Terminal) {
      String v = ((Terminal) value.getValue()).getName();
      Value val = globalSymbolTable.get(v);
      if (val == null) {
        Function currentFunction = callStack.peek();
        Value valueFromFunction = currentFunction.getValue(value);
        if (valueFromFunction == null) {
          throw new RuntimeException("The ID '" + value + "' doesn't exist "
                  + "on line " + curSeq.getLine());
        }
        return currentFunction.getValue(value);
      }
      return val;
    } else {
      return value;
    }
  }
  
  /**
   * Fetches a value from the tree by either accessing an ID, a CONST, or
   * evaluating an expression.
   *
   * @param cur The current tree node being searched
   * @param leftValue The left value of an expression
   * @return The value of the expression
   */
  public Value executeExpr(Node cur, Value leftValue) {
    Node parent = cur;
    ArrayList<Node> children = cur.getAll();
    double left, right;
    boolean bLeft, bRight;
    Boolean bReslt;
    Value rightValue;
    Double reslt;
    Symbol operator;
    int curID = cur.symbol().id();
    switch (curID) {
      case FUNC_CALL:
        return executeFunc(cur);
      case EXPRESSION:
        Node leftChild = cur.getLeftMostChild();
        if (leftChild == null) {
          return null;
        }
        switch (leftChild.symbol().id()) {
          case BOOL_TERM:
            leftValue = executeExpr(leftChild, leftValue);
            return executeExpr(cur.get(BOOL_TERM_TAIL), leftValue);
          case FUNC_CALL:
            return executeExpr(leftChild, leftValue);
          case ISSET_EXPR:
            return executeExpr(leftChild, leftValue);
        }
        Symbol symbolType = cur.getLeftMostChild().symbol();
        switch (symbolType.id()) {
          case CONCAT:
            StringBuilder concatValue = new StringBuilder();
            concatValue.append(executeExpr(cur.get(EXPRESSION), leftValue).valueToString());
            Node atomTailNode = cur.get(EXPR_LIST);
            while (atomTailNode.hasChildren()) {
              concatValue.append(executeExpr(atomTailNode.get(EXPRESSION), leftValue).valueToString());
              atomTailNode = atomTailNode.get(EXPR_LIST);
            }
            return new Value(concatValue.toString());
          case SLICE:
            String slice;
            slice = getValue(cur.get(EXPRESSION)).valueToString();
            int leftBound = getValue(cur.get(ARITHM_EXPR)).valueToDouble().intValue();
            int rightBound = getValue(cur.get(ARITHM_EXPR, 1)).valueToDouble().intValue();
            return new Value(slice.substring(leftBound, rightBound));
          case STRCMP:
            String v1,
             v2;
            v1 = getValue(cur.get(EXPRESSION)).valueToString();
            v2 = getValue(cur.get(EXPRESSION, 1)).valueToString();
            return new Value(v1.compareTo(v2));
          case FUNC:
            Node anonymousFunc = cur.get(ANONYMOUS_FUNC);
            return new Value(anonymousFunc);
          case ROUND_EXPR:
            Node roundExpr = cur.get(ROUND_EXPR);
            Value valueToRound = getValue(roundExpr.get(ID));
            double roundedValue = valueToRound.valueToDouble();
            int precision = getValue(roundExpr.get(ARITHM_EXPR)).valueToDouble().intValue();
            double factor = 1;
            while (precision > 0) {
              factor *= 10;
              precision--;
            }
            roundedValue = (double) Math.round(roundedValue * factor) / factor;
            return new Value(roundedValue);
          case ARRAY_EXPR:
            Node arrayExpression = cur.getLeftMostChild();
            Value firstValue = getValue(arrayExpression.get(EXPRESSION));
            Node arrayBody = arrayExpression.get(ARRAY_BODY);
            
            Node bodyType = arrayBody.get(ARRAY_LINEAR_BODY, false);
            if (bodyType == null) {
              bodyType = arrayBody.get(ARRAY_KEY_VALUE_BODY_TAIL);
              HashMap<String, Object> keypairArray = new HashMap<>();
              Value key = firstValue;
              Value keyValue = getValue(arrayBody.get(EXPRESSION));
              keypairArray.put(key.getValue().toString(), keyValue.getValue());

              Node bodyTail = arrayBody.get(ARRAY_KEY_VALUE_BODY_TAIL, false);
              arrayBody = bodyTail.get(ARRAY_KEY_VALUE_BODY, false);
              while(arrayBody != null && bodyType != null){
                key = getValue(bodyTail.get(EXPRESSION));
                keyValue = getValue(arrayBody.get(EXPRESSION));
                keypairArray.put(key.getValue().toString(), keyValue.getValue());
                bodyTail = arrayBody.get(ARRAY_KEY_VALUE_BODY_TAIL, false);
                arrayBody = bodyTail.get(ARRAY_KEY_VALUE_BODY, false);
              }
              return new Value(keypairArray);
              
            } else {
              List<Object> array = new ArrayList<>();
                Value arrayValue = firstValue;
                while (arrayValue != null){
                  array.add(arrayValue.getValue());
                  arrayValue = null;
                  if (arrayBody.hasChildren()) {
                    arrayValue = getValue(arrayBody.get(EXPRESSION));
                    arrayBody = arrayBody.get(ARRAY_LINEAR_BODY);
                  }
                }
                return new Value(array);
            }
          case GET:
            Value array = getValue(cur.get(EXPRESSION));
            if (array.getValue() instanceof List){
              List<Value> list = (List<Value>)array.getValue();
              Object arrayIndexValue = getValue(cur.get(EXPRESSION, 1)).getValue();
              int index;
              if (arrayIndexValue instanceof Integer){
                index = (Integer)arrayIndexValue;
              }else if (arrayIndexValue instanceof Double){
                index = ((Double)arrayIndexValue).intValue();
              } else {
                String strRep = (String) arrayIndexValue;
                index = Integer.parseInt(strRep);
              }
              return new Value(list.get(index));
            } else {
              HashMap<String, Object> map = (HashMap)array.getValue();
              Value key = getValue(cur.get(EXPRESSION, 1));
              Value keyValue = new Value(map.get(key.getValue().toString()));
              return keyValue;
            }
        }
        throw new RuntimeException("Couldn't get value, "
                + "undefined Node '" + cur.getLeftMostChild()+"' on line " + 
                cur.getLine());
      case ISSET_EXPR:
        Value resultBoolean = new Value(isset(cur.get(ID)));
        return resultBoolean;
      case BOOL_TERM_TAIL:
      case BOOL_FACTOR_TAIL:
        ArrayList<Node> boolChildren = cur.getAll();
        if (boolChildren.isEmpty()) {
          return leftValue;
        }
        cur = boolChildren.get(0);
        operator = cur.symbol();
        bLeft = fetch(leftValue, cur).valueToBoolean();
        cur = boolChildren.get(1);
        bRight = getValue(cur).valueToBoolean();
        switch (operator.id()) {
          case AND:
            bReslt = bLeft && bRight;
            break;
          case OR:
            bReslt = bLeft || bRight;
            break;
          default:
            bReslt = false;
        }
        cur = boolChildren.get(2);
        return executeExpr(cur, new Value(bReslt));
      case RELATION_OPTION:
        ArrayList<Node> relationChildren = cur.getAll();
        if (relationChildren.isEmpty()) {
          return leftValue;
        }
        cur = relationChildren.get(0);
        operator = cur.symbol();
      
        Object leftValueObject = fetch(leftValue, cur).getValue();
        cur = relationChildren.get(1);
        Object rightValueObject = getValue(cur).getValue();
        
        Boolean leftAsBool = null, rightAsBool = null;
        if (leftValueObject instanceof Boolean){
          leftAsBool = (Boolean)leftValueObject;
          left = 0;
        } else {
          left = fetch(leftValue, cur).valueToDouble();
        }
        if (rightValueObject instanceof Boolean){
          rightAsBool = (Boolean)rightValueObject;
          right = 0;
        } else {
          right = getValue(cur).valueToDouble();
        }
        
        bReslt = null;
        switch (operator.id()) {
          case LESS:
            bReslt = left < right;
            break;
          case LESSEQUAL:
            bReslt = left <= right;
            break;
          case EQUAL:
            if (leftAsBool != null && rightAsBool != null) {
              bReslt = Objects.equals(leftAsBool, rightAsBool);
            } else {
              bReslt = left == right;
            }
            break;
          case GREATEREQUAL:
            bReslt = left >= right;
            break;
          case GREATER:
            bReslt = left > right;
            break;
          case NOTEQUAL:
            if (leftAsBool != null && rightAsBool != null) {
              bReslt = !Objects.equals(leftAsBool, rightAsBool);
            } else {
              bReslt = left != right;
            }
            break;
        }
        return new Value(bReslt);
      case FACTOR:
        ArrayList<Node> factorChildren = cur.getAll();
        if (factorChildren.size() > 0) {
          cur = factorChildren.get(0);
          if (cur.symbol() instanceof Terminal) {
            operator = cur.symbol();
            cur = factorChildren.get(1);
            switch (operator.id()) {
              case MINUS:
                rightValue = (Value) executeExpr(cur, leftValue);
                left = leftValue.valueToDouble();
                right = rightValue.valueToDouble();
                reslt = left - right;
                return new Value(reslt);
              default:
                return executeExpr(cur, leftValue);
            }
          } else {
            return executeExpr(cur, leftValue);
          }
        } else {
          throw new RuntimeException("Factors not found");
        }
      case FACTOR_TAIL:
        ArrayList<Node> factorTailChildren = cur.getAll();
        if (factorTailChildren.isEmpty()) {
          return leftValue;
        }
        cur = factorTailChildren.get(0);
        if (cur.symbol() instanceof NonTerminal) {
          return executeExpr(cur, leftValue);
        }
        operator = cur.symbol();
        if (operator.id() == OPENPAREN) {
          cur = factorTailChildren.get(1);
          return executeExpr(cur, leftValue);
        }
        cur = factorTailChildren.get(1);
        left = fetch(leftValue, cur).valueToDouble();
        right = getValue(cur).valueToDouble();
        reslt = null;
        switch (operator.id()) {
          case MULTIPLY:
            reslt = left * right;
            break;
          case DIVIDE:
            if (right == 0){
              throw new ArithmeticException("Division by 0 on line "+
                      cur.getLine());
            }
            reslt = left / right;
            break;
          case POW:
            reslt = Math.pow(left, right);
            break;
          case MOD:
            if (right == 0){
              throw new ArithmeticException("Division by 0 on line "+
                      cur.getLine());
            }
            reslt = left % right;
            break;
        }
        return executeExpr(children.get(2), new Value(reslt));
      case TERM_TAIL:
        ArrayList<Node> termChildren = cur.getAll();
        if (termChildren.size() < 1) {
          return leftValue;
        }
        operator = termChildren.get(0).symbol();
        cur = termChildren.get(1);
        left = fetch(leftValue, cur).valueToDouble();
        right = getValue(cur).valueToDouble();
        reslt = null;
        switch (operator.id()) {
          case PLUS:
            reslt = left + right;
            break;
          case MINUS:
            reslt = left - right;
            break;
        }
        return executeExpr(termChildren.get(2), new Value(reslt));
      case TERM:
      case BOOL_TERM:
      case BOOL_FACTOR:
      case ARITHM_EXPR:
        leftValue = (Value) executeExpr(children.get(0), leftValue);
        return executeExpr(children.get(1), leftValue);
      case ID_OPTION:
      case PARAMETERS:
      case ATOMIC:
        return executeExpr(parent.getLeftMostChild(), leftValue);
      case ID:
        return new Value(cur.symbol());
      case CONST:
        return new Value(((Terminal) cur.symbol()).getName());
      default:
        return leftValue;
    }
  }

  public void collect(List<Symbol> program) {
    collectInOrder(program, root);
  }

  public void collectTerminals(List<Terminal> program) {
    collectTerminals(program, root);
  }

  /**
   * Grabs all the leaf nodes in the tree for execution.
   *
   * @param program
   * @param parent The root of the tree
   */
  private void collectTerminals(List<Terminal> program, Node parent) {
    List<Node> children = parent.getAll();
    for (int i = 0; i < children.size(); i++) {
      if (children.get(i).symbol() instanceof NonTerminal) {
        collectTerminals(program, children.get(i));
      } else {
        program.add((Terminal) children.get(i).symbol());
      }
    }
  }

  /**
   * Grabs all the leaf nodes in the tree for execution.
   *
   * @param program
   * @param parent The root of the tree
   */
  private void collectInOrder(List<Symbol> program, Node parent) {
    List<Node> children = parent.getAll();
    for (int i = 0; i < children.size(); i++) {
      program.add(children.get(i).symbol());
      collectInOrder(program, children.get(i));
    }
  }
  
  public void print() {
    print(root);
  }

  /**
   * Prints the tree
   *
   * @param parent from
   */
  public void print(Node parent) {
    List<Node> children = parent.getAll();
    for (int i = 0; i < children.size(); i++) {
      if (children.get(i).symbol() instanceof NonTerminal) {
        print(children.get(i));
      } else {
        System.out.println(children.get(i).symbol());
      }
    }
  }
}
