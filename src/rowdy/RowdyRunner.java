package rowdy;

import growdy.GRowdy;
import growdy.Node;
import growdy.NonTerminal;
import growdy.Symbol;
import growdy.Terminal;
import rowdy.exceptions.MainNotFoundException;
import rowdy.exceptions.ConstantReassignmentException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Stack;
import static rowdy.lang.RowdyGrammarConstants.*;

/**
 * Executes a parse tree given by a builder.
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
  
  private List<Value> programParamValues;
  
  private boolean firstTimeInitialization;

  public RowdyRunner() {
    this.root = null;
    main = null;
    callStack = new Stack<>();
    activeLoops = new Stack<>();
    globalSymbolTable = new HashMap<>();
    firstTimeInitialization = true;
  }
  
  public void initialize(GRowdy builder) {
    this.root = builder.getProgram();
    callStack.clear();
    activeLoops.clear();
    globalSymbolTable.clear();
  }
  
  public void initializeLine(GRowdy builder) {
    this.root = builder.getProgram();
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

  public void executeLine() throws ConstantReassignmentException {
    declareSystemConstants();
    executeStmt(root, null);
  }
  
  /**
   * Runs the program loaded into the parse tree. You need to first 
   * initialize the runner with a builder and flag if it's
   * a single line execution or a program file.
   *
   * @param programParams The program parameters
   * @throws MainNotFoundException
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public void execute(List<Value> programParams) throws MainNotFoundException, ConstantReassignmentException {
    this.programParamValues = programParams;
    declareSystemConstants();
    declareGlobals(root);
    if (main == null){
      throw new MainNotFoundException("main method not found");
    }
    executeStmt(main, null);
  }
  
  public void declareSystemConstants() throws ConstantReassignmentException {
    if (firstTimeInitialization) {
      setAsGlobal("true", new Value(true, true));
      setAsGlobal("false", new Value(false, true));
      setAsGlobal("null", new Value(null, true));
      firstTimeInitialization = false;
    }
  }

  /**
   * Scans the program looking for global variables and function declarations.
   * Each global is allocated in the main symbol table and functions are also
   * placed in the main symbol table.
   *
   * @param parent
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public void declareGlobals(Node parent) throws ConstantReassignmentException {
    Node currentTreeNode;
    ArrayList<Node> children = parent.getAll();
    int currentID;
    Value rightValue;
    
    for (int i = 0; i < children.size(); i++) {
      currentTreeNode = children.get(i);
      currentID = currentTreeNode.symbol().id();
      switch (currentID) {
        case ASSIGN_STMT:
          Terminal idTerminal = (Terminal) currentTreeNode.get(ID).symbol();
          rightValue = getValue(currentTreeNode.get(EXPRESSION));
          if (currentTreeNode.get(CONST_OPT).get(CONST_DEF, false) != null) {
            rightValue.setAsConstant(true);
          }
          allocate(idTerminal, rightValue);
          break;
        case FUNCTION:
          String functionName = ((Terminal) currentTreeNode.get(ID).symbol()).getName();
          if (functionName.equals("main")) {
            main = parent;
          }
          setAsGlobal(functionName, new Value(currentTreeNode, true));
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
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public void executeStmt(Node parent, Node seqControl) throws ConstantReassignmentException {
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
          if (currentTreeNode.get(CONST_OPT).get(CONST_DEF, false) != null) {
            rightValue.setAsConstant(true);
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
          Terminal loopIdTerminal = (Terminal) currentTreeNode.get(ID).symbol();
          String idName = (loopIdTerminal).getName();
          Function curFunction = null;
          if (!callStack.isEmpty()){
            curFunction = callStack.peek();
            Value curValue = curFunction.getValue(idName);
            if (curValue == null) {
              curFunction.allocate(idName, new Value(0));
            } else {
              throw new RuntimeException("ID '" + idName + "' already in use "+
                      "on line " + currentTreeNode.getLine());
            }
          } else {
            allocate(loopIdTerminal, new Value(0));
          }
          activeLoops.push(currentTreeNode);
          currentTreeNode.setSeqActive(true);
          boolean done = false;
          Node loopStmtList = currentTreeNode.get(STMT_BLOCK).get(STMT_LIST);
          while (!done) {
            executeStmt(loopStmtList, currentTreeNode);
            done = !currentTreeNode.isSeqActive();
          }
          if (curFunction != null){
            curFunction.unset(idName);
          }
          break;
        case BREAK_STMT:
          if (!currentTreeNode.get(ID_OPTION).hasSymbols()) {
            if (activeLoops.isEmpty()) {
              throw new RuntimeException("No loop to break. Line " 
                      + currentTreeNode.getLine());
            }
            Node idOption = activeLoops.peek();
            idName = ((Terminal) idOption.get(ID).symbol()).getName();
          } else {
            idName = ((Terminal) currentTreeNode.get(ID_OPTION).get(ID).symbol()).getName();
          }
          curFunction = null;
          if (!callStack.isEmpty()) {
            curFunction = callStack.peek();
            if (curFunction.getValue(idName) == null) {
              throw new RuntimeException("The ID '" + idName + "' doesn't exist."
                      + " Line " + currentTreeNode.getLine());
            }
          }
          for (;;) {
            Node lp = activeLoops.pop();
            lp.setSeqActive(false);
            String tempBinding = ((Terminal) lp.get(ID).symbol()).getName();
            if (curFunction != null){
              curFunction.unset(tempBinding);
            }
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
          if (currentTreeNode.hasSymbols()) {
            Node paramsTail = currentTreeNode.get(PARAMS_TAIL);
            while (paramsTail.hasSymbols()) {
              currentTreeNode = paramsTail.get(ID);
              Value v = executeExpr(currentTreeNode, null);
              inValue = keys.nextLine();
              allocate((Terminal) v.getValue(), new Value(inValue));
              paramsTail = paramsTail.get(PARAMS_TAIL);
            }
          }
          break;
        case FUNC_CALL:
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
          while (atomTailNode.hasSymbols()) {
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
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public Value executeFunc(Node cur) throws ConstantReassignmentException {
    // 1. Collect parameters
    Value funcVal = null;
    String funcName = ((Terminal) cur.get(ID).symbol()).getName();
    // FIXME Need to look at more than just the first function
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
    if (expr != null && expr.hasSymbols()) {
      parameterValues.add(getValue(cur.get(EXPRESSION)));
      Node atomTailNode = cur.get(EXPR_LIST);
      while (atomTailNode.hasSymbols()) {
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
      if (paramsNode.hasSymbols()) {
        Value paramValue = executeExpr(paramsNode, null);

        paramsList.add(((Terminal) paramValue.getValue()).getName());
        Node paramsTailNode = paramsNode.get(PARAMS_TAIL);
        while (paramsTailNode.hasSymbols()) {
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
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public void allocate(Terminal idTerminal, Value value) throws ConstantReassignmentException {
    Value exists = globalSymbolTable.get(idTerminal.getName());
    if (exists != null) {
      setAsGlobal(idTerminal, value);
    } else {
      
      if (callStack.isEmpty()) {
        setAsGlobal(idTerminal, value);
      } else {
        try {
          Stack<Function> searchStack = new Stack<>();
          while(!callStack.isEmpty()) {
            searchStack.push(callStack.pop());
          }
          boolean valueFound = false;
          while(!searchStack.isEmpty()) {
            Function currentFunction = searchStack.peek();
            Value v = currentFunction.getValue(idTerminal.getName());
            if (v != null && !valueFound) {
              currentFunction.allocate(idTerminal, value);
              valueFound = true;
            }
            callStack.push(searchStack.pop());
          }
          if (!valueFound) {
            Function currentFunction = callStack.peek();
            currentFunction.allocate(idTerminal, value);
          }
          
        } catch (EmptyStackException e) {}
      }
    }
  }

  /**
   * allocates/sets memory using the name of the symbol and a given value
   *
   * @param idName The variable being set
   * @param value The value of the variable
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public void setAsGlobal(String idName, Value value) throws ConstantReassignmentException {
    Value curValue;
    curValue = globalSymbolTable.get(idName);
    if (curValue == null) {
      globalSymbolTable.put(idName, value);
    } else {
      if (!curValue.isConstant()) {
        globalSymbolTable.remove(idName);
        globalSymbolTable.put(idName, value);
      } else {
        throw new ConstantReassignmentException(idName);
      }
    }
  }

  public void setAsGlobal(Terminal cur, Value value) throws ConstantReassignmentException {
    setAsGlobal(cur.getName(), value);
  }

  /**
   *
   * @param cur
   * @return
   * @throws ConstantReassignmentException
   */
  public boolean isset(Node cur) throws ConstantReassignmentException {
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
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public Value getValue(Node cur) throws ConstantReassignmentException {
    Value value = (Value) executeExpr(cur, null);
    return fetch(value, cur);
  }
  
  public Value fetch(Value value, Node curSeq) {
    if (value == null) {
      return null;
    }
    if (value.getValue() instanceof Terminal) {
      // Look in the functions first
      String fetchIdName = ((Terminal) value.getValue()).getName();
      Value valueFromFunction;
      Stack<Function> functions = new Stack<>();
      while(!callStack.isEmpty()) {
        Function currentFunction = callStack.pop();
        functions.push(currentFunction);
        valueFromFunction = currentFunction.getValue(value);
        if (valueFromFunction != null) {
          while(!functions.isEmpty()) {
            callStack.push(functions.pop());
          }
          return valueFromFunction;
        }
      }
      while(!functions.isEmpty()) {
        callStack.push(functions.pop());
      }
      Value val = globalSymbolTable.get(fetchIdName);
      if (val == null) {
        throw new RuntimeException("The ID '" + value + "' doesn't exist "
                + "on line " + curSeq.getLine());
      }
      return new Value(val);
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
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public Value executeExpr(Node cur, Value leftValue) throws ConstantReassignmentException {
    if (cur == null) {
      return leftValue;
    }
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
      case EXPRESSION:
        Node leftChild = cur.getLeftMost();
        if (leftChild == null) {
          return null;
        }
        switch (leftChild.symbol().id()) {
          case BOOL_EXPR:
            leftChild = leftChild.getLeftMost();
          case BOOL_TERM:
            leftValue = executeExpr(leftChild, leftValue);
            return executeExpr(cur.get(BOOL_TERM_TAIL, false), leftValue);
          case ISSET_EXPR:
            return executeExpr(leftChild, leftValue);
        }
        Symbol symbolType = cur.getLeftMost().symbol();
        switch (symbolType.id()) {
          case CONCAT_EXPR:
            StringBuilder concatValue = new StringBuilder();
            concatValue.append(executeExpr(cur.get(EXPRESSION), leftValue).valueToString());
            Node atomTailNode = cur.get(EXPR_LIST);
            while (atomTailNode.hasSymbols()) {
              concatValue.append(executeExpr(atomTailNode.get(EXPRESSION), leftValue).valueToString());
              atomTailNode = atomTailNode.get(EXPR_LIST);
            }
            return new Value(concatValue.toString());
          case SLICE_EXPR:
            String slice;
            slice = getValue(cur.get(EXPRESSION)).valueToString();
            int leftBound = getValue(cur.get(ARITHM_EXPR)).valueToDouble().intValue();
            int rightBound = getValue(cur.get(ARITHM_EXPR, 1)).valueToDouble().intValue();
            return new Value(slice.substring(leftBound, rightBound));
          case STRCMP_EXPR:
            String v1,
             v2;
            v1 = getValue(cur.get(EXPRESSION)).valueToString();
            v2 = getValue(cur.get(EXPRESSION, 1)).valueToString();
            return new Value(v1.compareTo(v2));
          case ANONYMOUS_FUNC:
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
            Node arrayExpression = cur.getLeftMost();
            Value firstValue = getValue(arrayExpression.get(EXPRESSION));
            Node arrayBody = arrayExpression.get(ARRAY_BODY);
            
            Node bodyType = arrayBody.get(ARRAY_LINEAR_BODY, false);
            if (bodyType == null) {
              bodyType = arrayBody.get(ARRAY_KEY_VALUE_BODY, false);
              
              if (bodyType == null) {
                List<Object> arrayList = new ArrayList<>(); 
                if (firstValue != null) {
                  arrayList.add(firstValue.getValue());
                }
                return new Value(arrayList);
              }
              
              HashMap<String, Object> keypairArray = new HashMap<>();
              Value key = firstValue;
              Value keyValue = getValue(bodyType.get(EXPRESSION));
              keypairArray.put(key.getValue().toString(), keyValue.getValue());
              arrayBody = bodyType;
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
                arrayBody = bodyType;
                while (arrayValue != null){
                  array.add(arrayValue.getValue());
                  arrayValue = null;
                  if (arrayBody != null && arrayBody.hasSymbols()) {
                    arrayValue = getValue(arrayBody.get(EXPRESSION));
                    arrayBody = arrayBody.get(ARRAY_LINEAR_BODY, false);
                  }
                }
                return new Value(array);
            }
          case GET_EXPR:
            Node getExpr = cur.getLeftMost();
            Value array = getValue(getExpr.get(EXPRESSION));
            if (array.getValue() instanceof List){
              List<Object> list = (List<Object>)array.getValue();
              Object arrayIndexValue = getValue(getExpr.get(EXPRESSION, 1)).getValue();
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
              Value key = getValue(getExpr.get(EXPRESSION, 1));
              Value keyValue = new Value(map.get(key.getValue().toString()));
              return keyValue;
            }
        }
        throw new RuntimeException("Couldn't get value, "
                + "undefined Node '" + cur.getLeftMost()+"' on line " + 
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
        Node firstRel = relationChildren.get(0);
        operator = firstRel.getLeftMost().symbol();
      
        Object leftValueObject = fetch(leftValue, firstRel).getValue();
        Node secondRel = firstRel.get(ARITHM_EXPR);
        Object rightValueObject = getValue(secondRel).getValue();
        
        Object leftAsBool = null, rightAsBool = null;
        left = 0;
        if (leftValueObject instanceof Boolean){
          leftAsBool = leftValueObject;
        } else if (leftValueObject instanceof Node) {
          leftAsBool = leftValueObject;
        } else {
          left = fetch(leftValue, secondRel).valueToDouble();
        }
        right = 0;
        if (rightValueObject instanceof Boolean){
          rightAsBool = rightValueObject;
        } else if (leftValueObject instanceof Node) {
          rightAsBool = rightValueObject;
        } else {
          right = getValue(secondRel).valueToDouble();
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
            if (leftAsBool != null || rightAsBool != null) {
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
                left = (leftValue != null)? leftValue.valueToDouble() : 0;
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
      case FACTOR_TAIL_MOD:
      case FACTOR_TAIL_POW:
      case FACTOR_TAIL_DIV:
      case FACTOR_TAIL_MUL:
        ArrayList<Node> factorTailChildren = cur.getAll();
        if (factorTailChildren.isEmpty()) {
          return leftValue;
        }
        Node factorTail = cur.getLeftMost();
        if (factorTail.symbol() instanceof NonTerminal) {
          return executeExpr(factorTail, leftValue);
        }
        operator = factorTail.symbol();
        if (operator.id() == OPENPAREN) {
          factorTail = factorTailChildren.get(1);
          return executeExpr(factorTail, leftValue);
        }
        factorTail = factorTailChildren.get(1);
        left = fetch(leftValue, factorTail).valueToDouble();
        right = getValue(factorTail).valueToDouble();
        reslt = null;
        switch (operator.id()) {
          case MULTIPLY:
            reslt = left * right;
            break;
          case DIVIDE:
            if (right == 0){
              throw new ArithmeticException("Division by 0 on line "+
                      factorTail.getLine());
            }
            reslt = left / right;
            break;
          case POW:
            reslt = Math.pow(left, right);
            break;
          case MOD:
            if (right == 0){
              throw new ArithmeticException("Division by 0 on line "+
                      factorTail.getLine());
            }
            reslt = left % right;
            break;
        }
        return executeExpr(children.get(2), new Value(reslt));
      case TERM_PLUS:
      case TERM_MINUS:
      case TERM_TAIL:
        Node leftMost = cur.getLeftMost();
        if (leftMost == null) {
          return leftValue;
        }
        ArrayList<Node> termChildren = leftMost.getAll();
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
      case PAREN_EXPR:  
        return executeExpr(parent.get(EXPRESSION), leftValue);
      case ID_OPTION:
      case PARAMETERS:
      case ATOMIC:
      case ATOMIC_ID:
      case ATOMIC_CONST:
      case ATOMIC_FUNC_CALL:
        return executeExpr(parent.getLeftMost(), leftValue);
      case ID:
        return new Value(cur.symbol());
      case CONST:
        return new Value(((Terminal) cur.symbol()).getName());
      case FUNC_CALL:
        return executeFunc(cur);
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
