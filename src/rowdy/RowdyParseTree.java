package rowdy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public Symbol getSymbol() {
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
    public Node getChild(int id) {
      return getChild(id, 0);
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
        if (children.get(c).getSymbol().getId() == id
                && occur == 0) {
          return children.get(c);
        } else if (children.get(c).getSymbol().getId() == id
                && occur > 0) {
          occur--;
        }
      }
      return null;
    }

    public ArrayList<Node> getChildren() {
      return children;
    }

    @Override
    public String toString() {
      return def.toString() + " " + children.toString();
    }
  }
  private Tokenizer parser;
  private Language language;
  private Node root;
  private Token currentToken;
  private int line;
  /**
   * Stores the name of each identifier currently allocated in RAM. Each id is
   * associated with an index which maps to the actual value of the id.
   */
  private final HashMap<String, Value> globalSymbolTable;
  /**
   * Keeps track of the level of loops the program is in.
   */
  private final Stack<Node> activeLoops;
  private final Stack<Function> callStack;
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
   * Builds the parse tree with the given program file and language definitions.
   * @param parser
   */
  public void build(Tokenizer parser) {
    this.parser = parser;
    NonTerminal program = (NonTerminal) language.getSymbol(PROGRAM);
    root = new Node(program);
    currentToken = this.parser.getToken();
    while (currentToken.getID() == 200) {
      if (currentToken.getID() == 200) {
        line++;
      }
      currentToken = this.parser.getToken();
    }
    int id = currentToken.getID();
    add(root, getRule(program, id));
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
      if (children.get(i).getSymbol() instanceof NonTerminal) {
        print(children.get(i));
      } else {
        System.out.println(children.get(i).getSymbol());
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
    Rule rule;
    List<Node> children = parent.getChildren();
    Node current;
    for (int i = 0; i < children.size(); i++) {
      current = children.get(i);
      symbol = current.getSymbol();
      if (symbol instanceof NonTerminal) {
        rule = getRule((NonTerminal) symbol, currentToken.getID());
        add(current, rule);
        build(current);
      } else {
        if (symbol.getId() != currentToken.getID()) {
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
  private Rule getRule(NonTerminal symbol, int terminal) {
    Hint productionHint = symbol.getHint(terminal);
    return language.getProductionRule(productionHint);
  }

  /**
   * Adds to the parent node the production rules. Each child is from the
   * production rule.
   *
   * @param parent The parent being added to
   * @param rule The production rule.
   */
  private void add(Node parent, Rule rule) {
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
  private void declareGlobals(Node parent, List<Value> programParams) {
    Node currentTreeNode;
    ArrayList<Node> children = parent.getChildren();
    int currentID;
    Value rightValue;
    for (int i = 0; i < children.size(); i++) {
      currentTreeNode = children.get(i);
      currentID = currentTreeNode.getSymbol().getId();
      switch (currentID) {
        case ASSIGN_STMT:
          rightValue = getValue(currentTreeNode.getChild(EXPRESSION));
          setAsGlobal((Terminal) currentTreeNode.getChild(ID).getSymbol(), rightValue);
          break;
        case FUNCTION:
          String idName = ((Terminal) currentTreeNode.getChild(ID).getSymbol()).getName();
          Node paramsNode = currentTreeNode.getChild(PARAMETERS);
          if (!idName.equals("main")) {
            setAsGlobal(idName, new Value(currentTreeNode));
          } else {
            if (globalSymbolTable.get(idName) != null) {
              throw new RuntimeException("main method already defined");
            }
            List<String> paramsList = new ArrayList<>();
            if (!programParams.isEmpty()) {
              paramsList.add(((Terminal) executeExpr(paramsNode.getChild(ID), null).getObject()).getName());
              Node n1 = paramsNode.getChild(PARAMS_TAIL);
              if (n1.hasChildren()) {
                if (n1.getChild(PARAMS_TAIL).getSymbol().getId() == PARAMS_TAIL) {
                  Node tail = n1.getChild(PARAMS_TAIL);
                  paramsList.add(((Terminal) executeExpr(n1.getChild(ID), null).getObject()).getName());
                  while (tail.hasChildren()) {
                    paramsList.add(((Terminal) executeExpr(tail.getChild(ID), null).getObject()).getName());
                    tail = tail.getChild(PARAMS_TAIL);
                  }
                }
              }
            }
            // 2. Copy actual parameters to formal parameters
            HashMap<String, Value> params = new HashMap<>();
            String paramName;
            for (int p = 0; p < paramsList.size(); p++) {
              paramName = paramsList.get(p);
              params.put(paramName, programParams.get(p));
            }
            Function function = new Function(idName, params);
            callStack.push(function);
            main = parent;
          }
          break;
        default:
          declareGlobals(currentTreeNode, programParams);
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
    ArrayList<Node> children = parent.getChildren();
    Value rightValue;
    for (int i = 0, curID; i < children.size(); i++) {
      currentTreeNode = children.get(i);
      curID = currentTreeNode.getSymbol().getId();
      switch (curID) {
        case FUNCTION:
          // Should only execute the main method
          executeStmt(currentTreeNode.getChild(STMT_LIST), null);
          // When main is finished, exit the program
          System.exit(0);
        case ASSIGN_STMT:
          Terminal idTerminal = (Terminal) currentTreeNode.getChild(ID).getSymbol();
          rightValue = getValue(currentTreeNode.getChild(EXPRESSION));
          allocate(idTerminal, rightValue);
          break;
        case IF_STMT:
          Node ifExpr = currentTreeNode.getChild(EXPRESSION);
          Value ifExprValue = (Value) executeExpr(ifExpr, null);
          if (ifExprValue.valueToBoolean()) {
            executeStmt(currentTreeNode.getChild(STMT_LIST), seqControl);
          } else {
            executeStmt(currentTreeNode.getChild(ELSE_PART), seqControl);
          }
          break;
        case LOOP_STMT:
          String idName = ((Terminal) currentTreeNode.getChild(ID).getSymbol()).getName();
          Value curValue = globalSymbolTable.get(idName);
          if (curValue == null) {
            globalSymbolTable.put(idName, new Value());
            activeLoops.push(currentTreeNode);
            currentTreeNode.seqActive = true;
          } else {
            throw new RuntimeException("Label '" + idName + "' already exists");
          }
          boolean done = false;
          Node loopStmtList = currentTreeNode.getChild(STMT_LIST);
          while (!done) {
            executeStmt(loopStmtList, currentTreeNode);
            curValue = globalSymbolTable.get(idName);
            done = (curValue == null);
          }
          break;
        case BREAK_STMT:
          if (!currentTreeNode.getChild(ID_OPTION).hasChildren()) {
            Node idOption = activeLoops.peek();
            idName = ((Terminal) idOption.getChild(ID).getSymbol()).getName();
          } else {
            idName = ((Terminal) currentTreeNode.getChild(ID_OPTION).getChild(ID).getSymbol()).getName();
          }
          if (globalSymbolTable.get(idName) == null) {
            throw new RuntimeException("The label '" + idName + "' is not used");
          }
          for (;;) {
            Node lp = activeLoops.pop();
            lp.seqActive = false;
            String tempBinding = ((Terminal) lp.getChild(ID).getSymbol()).getName();
            globalSymbolTable.remove(tempBinding);
            if (idName.equals(tempBinding)) {
              break;
            }
          }
          break;
        case ROUND_STMT:
          Value idToRound = getValue(currentTreeNode.getChild(ID));
          double val = idToRound.valueToNumber();
          int precision = getValueAsNumber(currentTreeNode.getChild(ARITHM_EXPR)).intValue();
          double factor = 1;
          while (precision > 0) {
            factor *= 10;
            precision--;
          }
          val = (double) Math.round(val * factor) / factor;
          allocate((Terminal) currentTreeNode.getChild(ID).getSymbol(), new Value(val));
          break;
        case READ_STMT:
          Scanner keys = new Scanner(System.in);
          String inValue;
          Node firstID = currentTreeNode.getChild(ID);
          Terminal t = (Terminal) executeExpr(firstID, null).getObject();
          allocate(t, new Value(keys.nextLine()));
          if (currentTreeNode.hasChildren()) {
            Node paramsTail = currentTreeNode.getChild(PARAMS_TAIL);
            while (paramsTail.hasChildren()) {
              currentTreeNode = paramsTail.getChild(ID);
              Value v = executeExpr(currentTreeNode, null);
              inValue = keys.nextLine();
              allocate((Terminal) v.getObject(), new Value(inValue));
              paramsTail = paramsTail.getChild(PARAMS_TAIL);
            }
          }
          break;
        case FUNC_CALL:
          String funcName = ((Terminal) currentTreeNode.getChild(ID).getSymbol()).getName();
          if (funcName.equals("main")) {
            throw new RuntimeException("Can't recur on main");
          }
          executeFunc(currentTreeNode);
          break;
        case RETURN_STMT:
          Function functionReturning = callStack.peek();
          seqControl.seqActive = false;
          Value toSet = getValue(currentTreeNode.getChild(EXPRESSION));
          functionReturning.setReturnValue(toSet);
          break;
        case PRINT_STMT:
          String printValue = "";
          printValue += getValueAsString(currentTreeNode.getChild(EXPRESSION));
          Node atomTailNode = currentTreeNode.getChild(EXPR_LIST);
          while (atomTailNode.hasChildren()) {
            printValue += getValueAsString(atomTailNode.getChild(EXPRESSION));
            atomTailNode = atomTailNode.getChild(EXPR_LIST);
          }
          char c;
          String temp = "";
          for (int l = 0; l < printValue.length(); l++) {
            c = printValue.charAt(l);
            if ((c == '\\') && (printValue.charAt(++l) == 'n')) {
              System.out.println(temp);
              temp = "";
            } else {
              temp += c;
            }
          }
          System.out.print(temp);
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
    String funcName = ((Terminal) cur.getChild(ID).getSymbol()).getName();
    if (globalSymbolTable.get(funcName) == null) {
      throw new RuntimeException("Function '" + funcName + "' not defined");
    }
    List<Value> ids = new ArrayList<>();
    if (cur.getChild(EXPRESSION).hasChildren()) {
      ids.add(getValue(cur.getChild(EXPRESSION)));
      Node atomTailNode = cur.getChild(EXPR_LIST);
      while (atomTailNode.hasChildren()) {
        ids.add(getValue(atomTailNode.getChild(EXPRESSION)));
        atomTailNode = atomTailNode.getChild(EXPR_LIST);
      }
    }
    Value funcVal = globalSymbolTable.get(funcName);
    Node functionNode = (Node) funcVal.getObject();
    List<String> paramsList = new ArrayList<>();
    if (!ids.isEmpty()) {
      Node paramsNode = functionNode.getChild(PARAMETERS);
      paramsList.add(((Terminal) executeExpr(paramsNode, null).getObject()).getName());
      Node paramsTailNode = paramsNode.getChild(PARAMS_TAIL);
      while (paramsTailNode.hasChildren()) {
        paramsList.add(((Terminal) executeExpr(paramsTailNode.getChild(ID), null).getObject()).getName());
        paramsTailNode = paramsTailNode.getChild(PARAMS_TAIL);
      }
    }
    // 2. Copy actual parameters to formal parameters
    HashMap<String, Value> params = new HashMap<>();
    String paramName;
    for (int p = 0; p < paramsList.size(); p++) {
      paramName = paramsList.get(p);
      params.put(paramName, ids.get(p));
    }
    // 3. Push the function onto the call stack
    Function function = new Function(funcName, params);
    callStack.push(function);
    // 4. Get and execute the stmt-list
    Node stmtList = functionNode.getChild(STMT_LIST), seqControl = new Node(null);
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
   * Allocates new memory for an ID. If the variable is not a global variable it
   * will allocate the variable to the current function.
   *
   * @param idTerminal
   * @param value
   */
  private void allocate(Terminal idTerminal, Value value) {
    Value exists = globalSymbolTable.get(idTerminal.getName());
    if (exists != null) {
      setAsGlobal(idTerminal, value);
    } else {
      Function currentFunction = callStack.peek();
      currentFunction.allocate(idTerminal, value);
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
    String v = ((Terminal) value.getObject()).getName();
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
    if (((Value) value).getObject() instanceof Terminal) {
      String v = ((Terminal) value.getObject()).getName();
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
    int curID = cur.getSymbol().getId();
    switch (curID) {
      case FUNC_CALL:
        return executeFunc(cur);
      case EXPRESSION:
        Node leftChild = cur.getLeftMostChild();
        if (leftChild == null) {
          return null;
        }
        switch (leftChild.getSymbol().getId()) {
          case BOOL_TERM:
            leftValue = executeExpr(leftChild, leftValue);
            return executeExpr(cur.getChild(BOOL_TERM_TAIL), leftValue);
          case FUNC_CALL:
            return executeExpr(leftChild, leftValue);
          case ISSET_EXPR:
            return executeExpr(leftChild, leftValue);
        }
        Symbol symbolType = cur.getLeftMostChild().getSymbol();
        switch (symbolType.getId()) {
          case CONCAT:
            String concatValue = "";
            concatValue += getValueAsString(executeExpr(cur.getChild(EXPRESSION), leftValue));
            Node atomTailNode = cur.getChild(EXPR_LIST);
            while (atomTailNode.hasChildren()) {
              concatValue += getValueAsString(executeExpr(atomTailNode.getChild(EXPRESSION), leftValue));
              atomTailNode = atomTailNode.getChild(EXPR_LIST);
            }
            return new Value(concatValue);
          case SLICE:
            String slice;
            slice = getValueAsString(cur.getChild(EXPRESSION));
            int leftBound = getValueAsNumber(cur.getChild(ARITHM_EXPR)).intValue();
            int rightBound = getValueAsNumber(cur.getChild(ARITHM_EXPR, 1)).intValue();
            return new Value(slice.substring(leftBound, rightBound));
          case STRCMP:
            String v1,
             v2;
            v1 = getValueAsString(cur.getChild(EXPRESSION));
            v2 = getValueAsString(cur.getChild(EXPRESSION, 1));
            return new Value(v1.compareTo(v2));
        }
      case ISSET_EXPR:
        Value resultBoolean = new Value(isset(cur.getChild(ID)));
        return resultBoolean;
      case BOOL_TERM_TAIL:
      case BOOL_FACTOR_TAIL:
        ArrayList<Node> boolChildren = cur.getChildren();
        if (boolChildren.isEmpty()) {
          return leftValue;
        }
        cur = boolChildren.get(0);
        operator = cur.getSymbol();
        bLeft = getValueAsBoolean(leftValue);
        cur = boolChildren.get(1);
        bRight = getValueAsBoolean(cur);
        switch (operator.getId()) {
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
        operator = cur.getSymbol();
        left = getValueAsNumber(leftValue);
        cur = relationChildren.get(1);
        right = getValueAsNumber(cur);
        bReslt = null;
        switch (operator.getId()) {
          case LESS:
            bReslt = left < right;
            break;
          case LESSEQUAL:
            bReslt = left <= right;
            break;
          case EQUAL:
            bReslt = left == right;
            break;
          case GREATEREQUAL:
            bReslt = left >= right;
            break;
          case GREATER:
            bReslt = left > right;
            break;
          case NOTEQUAL:
            bReslt = left != right;
            break;
        }
        return new Value(bReslt);
      case FACTOR:
        ArrayList<Node> factorChildren = cur.getChildren();
        if (factorChildren.size() > 0) {
          cur = factorChildren.get(0);
          if (cur.getSymbol() instanceof Terminal) {
            operator = cur.getSymbol();
            cur = factorChildren.get(1);
            switch (operator.getId()) {
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
        if (cur.getSymbol() instanceof NonTerminal) {
          return executeExpr(cur, leftValue);
        }
        operator = cur.getSymbol();
        if (operator.getId() == OPENPAREN) {
          cur = factorTailChildren.get(1);
          return executeExpr(cur, leftValue);
        }
        cur = factorTailChildren.get(1);
        left = getValueAsNumber(leftValue);
        right = getValueAsNumber(cur);
        reslt = null;
        switch (operator.getId()) {
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
        operator = termChildren.get(0).getSymbol();
        cur = termChildren.get(1);
        left = getValueAsNumber(leftValue);
        right = getValueAsNumber(cur);
        reslt = null;
        switch (operator.getId()) {
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
        return new Value(cur.getSymbol());
      case CONST:
        return new Value(((Terminal) cur.getSymbol()).getName());
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
      if (children.get(i).getSymbol() instanceof NonTerminal) {
        collectTerminals(program, children.get(i));
      } else {
        program.add((Terminal) children.get(i).getSymbol());
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
      program.add(children.get(i).getSymbol());
      collectInOrder(program, children.get(i));
    }
  }
}
