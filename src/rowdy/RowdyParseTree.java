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
public class RowdyParseTree {

  private Tokenizer parser;
  private Language language;
  private Node root;
  private Token currentToken;
  private int line;
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

  public RowdyParseTree(Language language) {
    this.language = language;
    this.root = null;
    this.line = 1;
    main = null;
    callStack = new Stack<>();
    activeLoops = new Stack<>();
    globalSymbolTable = new HashMap<>();
  }

  public void setLanguage(Language language) {
    this.language = language;
  }

  /**
   * Only call this method if the program has stopped executing.
   */
  public void dumpCallStack() {
    System.out.print("Exception in: ");
    while(!callStack.isEmpty()){
      System.out.println("->" + callStack.pop().getName());
    }
  }
  
  /**
   * Builds the parse tree with the given program file and language definitions.
   * @param parser
   */
  public void build(Tokenizer parser) {
    this.parser = parser;
    NonTerminal program = (NonTerminal) language.getSymbol(PROGRAM);
    root = new Node(program);
    currentToken = this.parser.getToken();
    if (currentToken == null){
      return;
    }
    while (currentToken.getID() == 200) {
      if (currentToken.getID() == 200) {
        line++;
      }
      currentToken = this.parser.getToken();
      if (currentToken == null){
        return;
      }
    }
    int id = currentToken.getID();
    addToNode(root, produce(program, id));
    build(root);
  }

  public void print() {
    print(root);
  }

  /**
   * Prints the tree
   *
   * @param parent from
   */
  private void print(Node parent) {
    List<Node> children = parent.getChildren();
    for (int i = 0; i < children.size(); i++) {
      if (children.get(i).symbol() instanceof NonTerminal) {
        print(children.get(i));
      } else {
        System.out.println(children.get(i).symbol());
      }
    }
  }

  /**
   * Walks through the tree recursively building on non-terminals. If a syntax
   * error is detected the line number is printed out.
   *
   * @param parent
   */
  private void build(Node parent) {
    Symbol symbol;
    ProductionSymbols rule;
    List<Node> children = parent.getChildren();
    Node current;
    for (int i = 0; i < children.size(); i++) {
      current = children.get(i);
      symbol = current.symbol();
      if (symbol instanceof NonTerminal) {
        if (currentToken == null) break;
        rule = produce((NonTerminal) symbol, currentToken.getID());
        addToNode(current, rule);
        build(current);
      } else {
        if (symbol.id() != currentToken.getID()) {
          throw new RuntimeException("Syntax error, unexpected token '"
                  + currentToken.getSymbol() + "' on Line " + line);
        }
        children.remove(i);
        Terminal terminal = new Terminal(symbol.getName(), currentToken.getID(), currentToken.getSymbol());
        children.add(i, new Node(terminal));
        currentToken = parser.getToken();
        while (currentToken != null && isCurTokenEOLN()) {
          if (isCurTokenEOLN()) {
            line++;
          }
          currentToken = parser.getToken();
        }
        if (currentToken == null) break;
      }
    }
  }

  private boolean isCurTokenEOLN() {
    return currentToken.getID() == 200;
  }

  /**
   * Builds a rule from the given NonTerminal using the id to map onto a hint.
   *
   * @param symbol The NonTerminal for reference
   * @param terminal The id from a token, usually a terminal
   * @return Fetches a production rule from the language's grammar.
   */
  private ProductionSymbols produce(NonTerminal symbol, int terminal) {
    Hint productionHint = symbol.getHint(terminal);
    return language.getProductionSymbols(productionHint);
  }

  /**
   * Adds to the parent node the production rules. Each child is from the
   * production rule.
   *
   * @param parent The parent being added to
   * @param rule The production rule.
   */
  private void addToNode(Node parent, ProductionSymbols rule) {
    Symbol[] symbols = rule.getSymbols();
    for (Symbol symbol : symbols) {
      Node node = new Node(symbol);
      parent.addChild(node);
    }
  }

  /**
   * Runs the program loaded into the parse tree.
   *
   * @param programParams The program parameters
   * @throws java.lang.Exception
   */
  public void execute(List<Value> programParams) {
    // Declare global variables
    declareGlobals(root, programParams);
    if (main == null) {
      throw new RuntimeException("main method not found");
    }
    // Run the main method
    executeStmt(main, null);
  }

  /**
   * Scans the program looking for global variables and function declarations.
   * Each global is allocated in the main symbol table and functions are also
   * placed in the main symbol table.
   *
   * @param parent
   */
  private void declareGlobals(Node parent, List<Value> programParamValues) {
    Node currentTreeNode;
    ArrayList<Node> children = parent.getChildren();
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
          Node paramsNode = currentTreeNode.get(FUNCTION_BODY).get(PARAMETERS);
          if (!functionName.equals("main")) {
            setAsGlobal(functionName, new Value(currentTreeNode));
          } else {
            if (globalSymbolTable.get(functionName) != null) {
              throw new RuntimeException("main method already defined");
            }
            List<String> paramsList = new ArrayList<>();
            if (!programParamValues.isEmpty()) {
              paramsList.add(((Terminal) executeExpr(paramsNode.get(ID), null).getValue()).getName());
              Node paramTail = paramsNode.get(PARAMS_TAIL);
              if (paramTail.hasChildren()) {
                if (paramTail.get(PARAMS_TAIL).symbol().id() == PARAMS_TAIL) {
                  Node tail = paramTail.get(PARAMS_TAIL);
                  paramsList.add(((Terminal) executeExpr(paramTail.get(ID), null).getValue()).getName());
                  while (tail.hasChildren()) {
                    paramsList.add(((Terminal) executeExpr(tail.get(ID), null).getValue()).getName());
                    tail = tail.get(PARAMS_TAIL);
                  }
                }
              }
            }
            // 2. Copy actual parameters to formal parameters
            HashMap<String, Value> params = new HashMap<>();
            String paramName;
            for (int p = 0; p < paramsList.size(); p++) {
              paramName = paramsList.get(p);
              params.put(paramName, programParamValues.get(p));
            }
            Function function = new Function(functionName, params);
            callStack.push(function);
            main = parent;
          }
          break;
        default:
          declareGlobals(currentTreeNode, programParamValues);
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
  private void executeStmt(Node parent, Node seqControl) {
    Node currentTreeNode;
    if (parent == null) 
      throw new IllegalArgumentException("parent node is null");
    ArrayList<Node> children = parent.getChildren();
    Value rightValue;
    for (int i = 0, curID; i < children.size(); i++) {
      currentTreeNode = children.get(i);
      curID = currentTreeNode.symbol().id();
      switch (curID) {
        case FUNCTION:
          // This should only execute the main function
          Node funcStmtBlock = currentTreeNode.get(FUNCTION_BODY).get(STMT_BLOCK);
          Node funcStmtList = funcStmtBlock.get(STMT_LIST);
          executeStmt(funcStmtList, null);
          // After main has finished executing, the program is finished.
          System.exit(0);
        case ASSIGN_STMT:
          Terminal idTerminal = (Terminal) currentTreeNode.get(ID).symbol();
          rightValue = getValue(currentTreeNode.get(EXPRESSION));
          String idNameToAssign = idTerminal.getName();
          if (idNameToAssign.equals("true") || idNameToAssign.equals("false")) {
            throw new RuntimeException("Can't assign new value to constant '" + idNameToAssign + "'");
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
          Value curValue = curFunction.getValue(idName, false);
          if (curValue == null) {
            curFunction.allocate(idName, new Value(0));
            activeLoops.push(currentTreeNode);
            currentTreeNode.seqActive = true;
          } else {
            throw new RuntimeException("ID '" + idName + "' already in use");
          }
          boolean done = false;
          Node loopStmtList = currentTreeNode.get(STMT_BLOCK).get(STMT_LIST);
          while (!done) {
            executeStmt(loopStmtList, currentTreeNode);
            done = !currentTreeNode.seqActive;
          }
          curFunction.unset(idName);
          break;
        case BREAK_STMT:
          if (!currentTreeNode.get(ID_OPTION).hasChildren()) {
            if (activeLoops.isEmpty()) {
              throw new RuntimeException("No loop to break");
            }
            Node idOption = activeLoops.peek();
            idName = ((Terminal) idOption.get(ID).symbol()).getName();
          } else {
            idName = ((Terminal) currentTreeNode.get(ID_OPTION).get(ID).symbol()).getName();
          }
          curFunction = callStack.peek();
          if (curFunction.getValue(idName, false) == null) {
            throw new RuntimeException("The ID '" + idName + "' doesn't exist");
          }
          for (;;) {
            Node lp = activeLoops.pop();
            lp.seqActive = false;
            String tempBinding = ((Terminal) lp.get(ID).symbol()).getName();
            curFunction.unset(tempBinding);
            if (idName.equals(tempBinding)) {
              break;
            }
          }
          break;
        case ROUND_STMT:
          Value idToRound = getValue(currentTreeNode.get(ID));
          double val = idToRound.valueToNumber();
          int precision = getValueAsNumber(currentTreeNode.get(ARITHM_EXPR)).intValue();
          double factor = 1;
          while (precision > 0) {
            factor *= 10;
            precision--;
          }
          val = (double) Math.round(val * factor) / factor;
          allocate((Terminal) currentTreeNode.get(ID).symbol(), new Value(val));
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
            throw new RuntimeException("Can't recurse on main");
          }
          executeFunc(currentTreeNode);
          break;
        case RETURN_STMT:
          Function functionReturning = callStack.peek();
          seqControl.seqActive = false;
          Value toSet = getValue(currentTreeNode.get(EXPRESSION));
          functionReturning.setReturnValue(toSet);
          break;
        case PRINT_STMT:
          StringBuilder printValue = new StringBuilder();
          printValue.append(getValueAsString(currentTreeNode.get(EXPRESSION)));
          Node atomTailNode = currentTreeNode.get(EXPR_LIST);
          while (atomTailNode.hasChildren()) {
            printValue.append(getValueAsString(atomTailNode.get(EXPRESSION)));
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
            if (seqControl.seqActive) {
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
  private Value executeFunc(Node cur) {
    // 1. Collect parameters
    Value funcVal = null;
    String funcName = ((Terminal) cur.get(ID).symbol()).getName();
    if (!callStack.isEmpty()){
      funcVal = callStack.peek().getValue(funcName, false);
    } 
    if (funcVal == null) {
      if (globalSymbolTable.get(funcName) == null) {
        throw new RuntimeException("Function '" + funcName + "' not defined");
      } else {
        funcVal = globalSymbolTable.get(funcName);
      }
    }
    List<Value> parameterValues = new ArrayList<>();
    if (cur.get(EXPRESSION).hasChildren()) {
      parameterValues.add(getValue(cur.get(EXPRESSION)));
      Node atomTailNode = cur.get(EXPR_LIST);
      while (atomTailNode.hasChildren()) {
        parameterValues.add(getValue(atomTailNode.get(EXPRESSION)));
        atomTailNode = atomTailNode.get(EXPR_LIST);
      }
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
    Function function = new Function(funcName, params);
    callStack.push(function);
    // 4. Get and execute the stmt-list
    Node funcStmtBlock = functionNode.get(FUNCTION_BODY).get(STMT_BLOCK);
    Node stmtList = funcStmtBlock.get(STMT_LIST), seqControl = new Node(null);
    seqControl.seqActive = true;
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
  private void allocate(Terminal idTerminal, Value value) {
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
  private void setAsGlobal(String idName, Value value) {
    Value curValue;
    curValue = globalSymbolTable.get(idName);
    if (curValue == null) {
      globalSymbolTable.put(idName, value);
    } else {
      globalSymbolTable.remove(idName);
      globalSymbolTable.put(idName, value);
    }
  }

  private void setAsGlobal(Terminal cur, Value value) {
    setAsGlobal(cur.getName(), value);
  }

  private boolean isset(Node cur) {
    Value o = (Value) executeExpr(cur, null);
    return isset(o);
  }

  private boolean isset(Value value) {
    if (value == null) {
      return false;
    }
    String v = ((Terminal) value.getValue()).getName();
    Value val = globalSymbolTable.get(v);
    if (val == null) {
      Function currentFunction = callStack.peek();
      return (currentFunction.getValue(value, false) != null);
    }
    return true;
  }

  /**
   * Fetches the value stored or returns 'value' if no ID is stored in it.
   *
   * @param value The Value object that holds an atomic symbol. May contain an
   * ID or it may be a Constant.
   * @return A value object with an atomic symbol stored in it.
   */
  private Value getValue(Value value) {
    if (value == null) {
      return null;
    }
    if (((Value) value).getValue() instanceof Terminal) {
      String v = ((Terminal) value.getValue()).getName();
      Value val = globalSymbolTable.get(v);
      if (val == null) {
        Function currentFunction = callStack.peek();
        return currentFunction.getValue(value, true);
      }
      return val;
    } else {
      return value;
    }
  }

  /**
   * Performs the same task as getValue(Value) but uses a tree node to access
   * the atom.
   *
   * @param cur The tree node
   * @return A value object with an atom stored in it.
   */
  private Value getValue(Node cur) {
    Value o = (Value) executeExpr(cur, null);
    return getValue(o);
  }

  private String getValueAsString(Node cur) {
    return getValue(cur).valueToString().replaceAll("\"", "");
  }

  private String getValueAsString(Value cur) {
    return getValue(cur).valueToString().replaceAll("\"", "");
  }

  private Boolean getValueAsBoolean(Node cur) {
    return getValue(cur).valueToBoolean();
  }

  private Double getValueAsNumber(Node cur) {
    return getValue(cur).valueToNumber();
  }

  private Boolean getValueAsBoolean(Value cur) {
    return getValue(cur).valueToBoolean();
  }

  private Double getValueAsNumber(Value cur) {
    return getValue(cur).valueToNumber();
  }

  /**
   * Fetches a value from the tree by either accessing an ID, a CONST, or
   * evaluating an expression.
   *
   * @param cur The current tree node being searched
   * @param leftValue The left value of an expression
   * @return The value of the expression
   */
  private Value executeExpr(Node cur, Value leftValue) {
    Node parent = cur;
    ArrayList<Node> children = cur.getChildren();
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
            String concatValue = "";
            concatValue += getValueAsString(executeExpr(cur.get(EXPRESSION), leftValue));
            Node atomTailNode = cur.get(EXPR_LIST);
            while (atomTailNode.hasChildren()) {
              concatValue += getValueAsString(executeExpr(atomTailNode.get(EXPRESSION), leftValue));
              atomTailNode = atomTailNode.get(EXPR_LIST);
            }
            return new Value(concatValue);
          case SLICE:
            String slice;
            slice = getValueAsString(cur.get(EXPRESSION));
            int leftBound = getValueAsNumber(cur.get(ARITHM_EXPR)).intValue();
            int rightBound = getValueAsNumber(cur.getChild(ARITHM_EXPR, 1)).intValue();
            return new Value(slice.substring(leftBound, rightBound));
          case STRCMP:
            String v1,
             v2;
            v1 = getValueAsString(cur.get(EXPRESSION));
            v2 = getValueAsString(cur.getChild(EXPRESSION, 1));
            return new Value(v1.compareTo(v2));
          case FUNC:
            Node anonymousFunc = cur.get(ANONYMOUS_FUNC);
            return new Value(anonymousFunc);
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
              Object arrayIndexValue = getValue(cur.getChild(EXPRESSION, 1)).getValue();
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
              Value key = getValue(cur.getChild(EXPRESSION, 1));
              Value keyValue = new Value(map.get(key.getValue().toString()));
              return keyValue;
            }
        }
        throw new RuntimeException("Couldn't get value, "
                + "undefined Node '" + cur.getLeftMostChild()+"'");
      case ISSET_EXPR:
        Value resultBoolean = new Value(isset(cur.get(ID)));
        return resultBoolean;
      case BOOL_TERM_TAIL:
      case BOOL_FACTOR_TAIL:
        ArrayList<Node> boolChildren = cur.getChildren();
        if (boolChildren.isEmpty()) {
          return leftValue;
        }
        cur = boolChildren.get(0);
        operator = cur.symbol();
        bLeft = getValueAsBoolean(leftValue);
        cur = boolChildren.get(1);
        bRight = getValueAsBoolean(cur);
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
        ArrayList<Node> relationChildren = cur.getChildren();
        if (relationChildren.isEmpty()) {
          return leftValue;
        }
        cur = relationChildren.get(0);
        operator = cur.symbol();
        
        Object leftValueObject = getValue(leftValue).getValue();
        cur = relationChildren.get(1);
        Object rightValueObject = getValue(cur).getValue();
        
        Boolean leftAsBool = null, rightAsBool = null;
        if (leftValueObject instanceof Boolean){
          leftAsBool = (Boolean)leftValueObject;
          left = 0;
        } else {
          left = getValueAsNumber(leftValue);
        }
        if (rightValueObject instanceof Boolean){
          rightAsBool = (Boolean)leftValueObject;
          right = 0;
        } else {
          right = getValueAsNumber(cur);
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
        ArrayList<Node> factorChildren = cur.getChildren();
        if (factorChildren.size() > 0) {
          cur = factorChildren.get(0);
          if (cur.symbol() instanceof Terminal) {
            operator = cur.symbol();
            cur = factorChildren.get(1);
            switch (operator.id()) {
              case MINUS:
                rightValue = (Value) executeExpr(cur, leftValue);
                left = leftValue.valueToNumber();
                right = rightValue.valueToNumber();
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
        ArrayList<Node> factorTailChildren = cur.getChildren();
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
        left = getValueAsNumber(leftValue);
        right = getValueAsNumber(cur);
        reslt = null;
        switch (operator.id()) {
          case MULTIPLY:
            reslt = left * right;
            break;
          case DIVIDE:
            reslt = left / right;
            break;
          case POW:
            reslt = Math.pow(left, right);
            break;
          case MOD:
            reslt = left % right;
            break;
        }
        return executeExpr(children.get(2), new Value(reslt));
      case TERM_TAIL:
        ArrayList<Node> termChildren = cur.getChildren();
        if (termChildren.size() < 1) {
          return leftValue;
        }
        operator = termChildren.get(0).symbol();
        cur = termChildren.get(1);
        left = getValueAsNumber(leftValue);
        right = getValueAsNumber(cur);
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
    List<Node> children = parent.getChildren();
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
    List<Node> children = parent.getChildren();
    for (int i = 0; i < children.size(); i++) {
      program.add(children.get(i).symbol());
      collectInOrder(program, children.get(i));
    }
  }
  
  /**
   * Tree Node for the parse tree
   */
  private class Node {

    private final ArrayList<Node> children;
    private final Symbol def;
    private Boolean seqActive;

    public Node(Symbol def) {
      children = new ArrayList<>();
      this.def = def;
      seqActive = false;
    }

    public void addChild(Node child) {
      children.add(child);
    }

    public Symbol symbol() {
      return def;
    }

    public boolean hasChildren() {
      return !children.isEmpty();
    }

    public Node getLeftMostChild() {
      if (children.isEmpty()) {
        return null;
      }
      return children.get(0);
    }

    /**
     * Gets the child node with the id, only returns the first occurance of the
     * found child.
     *
     * @param id The child's ID to search for
     * @return The found child, null if nothing was found
     */
    public Node get(int id) {
      return getChild(id, 0);
    }
    
    public Node get(int id, boolean throwException) {
      if (throwException){
        return getChild(id, 0);
      } else {
        try {
          return getChild(id, 0);
        }catch (RuntimeException re){
          return null;
        }
      }
    }

    /**
     * Gets the child node with the id. Since there could be duplicates or
     * multiple child nodes with the same ID, occur will tell the method to skip
     * a certain number of occurrences of the given ID. If occur is 1 then it
     * will skip the first occurrence of the search.
     *
     * @param id The id to search for
     * @param occur The number of times to skip a duplicate
     * @return The child node of this parent, null if it doesn't exist.
     */
    public Node getChild(int id, int occur) {
      for (int c = 0; c < children.size(); c++) {
        if (children.get(c).symbol().id() == id
                && occur == 0) {
          return children.get(c);
        } else if (children.get(c).symbol().id() == id
                && occur > 0) {
          occur--;
        }
      }
      throw new RuntimeException("The id '" + id + 
              "' could not be found for the node '" + def + "'");
    }

    public ArrayList<Node> getChildren() {
      return children;
    }

    @Override
    public String toString() {
      return def.toString() + " " + 
              ((children.isEmpty())?"":(children.toString()));
    }
  }
}
