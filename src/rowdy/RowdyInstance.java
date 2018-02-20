package rowdy;

import growdy.GRowdy;
import growdy.Node;
import growdy.NonTerminal;
import growdy.Symbol;
import growdy.Terminal;
import rowdy.exceptions.MainNotFoundException;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.*;
import rowdy.nodes.statement.*;
import rowdy.nodes.RowdyNode;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import static rowdy.lang.RowdyGrammarConstants.*;


/**
 * Executes a parse tree given by a builder.
 *
 * @author Richard DeSilvey
 */
public class RowdyInstance {

  private Node root;
  /**
   * Stores the name of each identifier or function
   */
  public final HashMap<String, Value> globalSymbolTable;
  /**
   * Keeps track of the level of loops the program is in.
   */
  public final Stack<Node> activeLoops;
  /**
   * Keeps track of the functions currently being called.
   */
  public final Stack<Function> callStack;
  /**
   * Reference to the main function
  */
  private Node main;
  
  private List<Value> programParamValues;
  
  private boolean firstTimeInitialization;

  public RowdyInstance() {
    this.root = null;
    main = null;
    callStack = new Stack<>();
    activeLoops = new Stack<>();
    globalSymbolTable = new HashMap<>();
    firstTimeInitialization = true;
  }
  
  public void initialize(GRowdy builder) {
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
  
  public void declareGlobals() throws ConstantReassignmentException {
    this.declareGlobals(root);
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
    
    for (int i = 0; i < children.size(); i++) {
      currentTreeNode = children.get(i);
      currentID = currentTreeNode.symbol().id();
      switch (currentID) {
        case ASSIGN_STMT:
          ((AssignStatement) currentTreeNode).execute(null);
          break;
        case FUNCTION:
          Node nativeOpt = currentTreeNode.get(NATIVE_FUNC_OPT);
          String functionName = ((Terminal) currentTreeNode.get(ID).symbol()).getName();
          if (nativeOpt.hasSymbols()) {
            setAsGlobal(functionName, new Value());
          } else {
            if (functionName.equals("main")) {
              main = parent;
            }
            setAsGlobal(functionName, new Value(currentTreeNode, true));
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
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public void executeStmt(Node parent, Node seqControl) throws ConstantReassignmentException {
    RowdyNode currentTreeNode;
    if (parent == null) 
      throw new IllegalArgumentException("parent node is null");
    ArrayList<Node> children = parent.getAll();
    for (int i = 0, curID; i < children.size(); i++) {
      currentTreeNode = (RowdyNode) children.get(i);
      curID = currentTreeNode.symbol().id();
      switch (curID) {
        case FUNCTION:
          Value exitValue = executeFunc(currentTreeNode);
          if (exitValue == null){
            exitValue = new Value(0, false);
          }
          System.exit(exitValue.valueToDouble().intValue());
        case ASSIGN_STMT:
        case LOOP_STMT:
        case BREAK_STMT:
          currentTreeNode.execute(null);
          break;
        case IF_STMT:
          ((IfStatement) currentTreeNode).execute(new Value(seqControl, false));
          break;
        
        case READ_STMT:
          ((ReadStatement) currentTreeNode).execute(new Value(System.in, false));
          break;
        case FUNC_CALL:
          executeFunc(currentTreeNode);
          break;
        case RETURN_STMT:
          ((ReturnStatement) currentTreeNode).execute(new Value(seqControl, false));
          break;
        case PRINT_STMT:
          ((PrintStatement) currentTreeNode).execute(new Value(System.out, false));
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

  public void allocateToCurrentFunction(Terminal idTerminal, Value value) throws ConstantReassignmentException {
    Function currentFunction = callStack.peek();
    currentFunction.allocate(idTerminal, value);
  }
  
  public Value executeFunc(Node cur) throws ConstantReassignmentException {
    String funcName = ((Terminal) cur.get(ID).symbol()).getName();
    List<Value> parameterValues = new ArrayList<>();
    Node expr = cur.get(EXPRESSION, false);// This might be 'main'
    if (expr != null && expr.hasSymbols()) {
      Expression paramValue = (Expression)cur.get(EXPRESSION);
      parameterValues.add(paramValue.execute());
      Node atomTailNode = cur.get(EXPR_LIST);
      while (atomTailNode.hasSymbols()) {
        paramValue = (Expression)atomTailNode.get(EXPRESSION);
        parameterValues.add(paramValue.execute());
        atomTailNode = atomTailNode.get(EXPR_LIST);
      }
    } else if (funcName.equals("main")){
      parameterValues = programParamValues;
    }
    return executeFunc(cur, parameterValues);
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
   * @param parameterValues
   * @return The function's return value
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public Value executeFunc(Node cur, List<Value> parameterValues) throws ConstantReassignmentException {
    // 1. Collect parameters
    Value funcVal;
    String funcName = ((Terminal) cur.get(ID).symbol()).getName();
    
    funcVal = fetch(getIdAsValue(cur.get(ID)), cur);
    if (funcVal == null) {
      if (globalSymbolTable.get(funcName) == null) {
        throw new RuntimeException("Function '" + funcName + "' not defined on "
                + "line " + cur.getLine());
      } else {
        funcVal = globalSymbolTable.get(funcName);
      }
    }
    
    if (funcVal.getValue() instanceof RowdyNode) {
      Node functionNode = (Node) funcVal.getValue();
      List<String> paramsList = new ArrayList<>();
      if (!parameterValues.isEmpty()) {
        Node functionBody = functionNode.get(FUNCTION_BODY);
        Node paramsNode = functionBody.get(PARAMETERS);
        if (paramsNode.hasSymbols()) {
          paramsList.add(((Terminal) paramsNode.get(ID).symbol()).getName());
          Node paramsTailNode = paramsNode.get(PARAMS_TAIL);
          while (paramsTailNode.hasSymbols()) {
            paramsList.add(((Terminal) paramsTailNode.get(ID).symbol()).getName());
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
    } else {
      NativeJava nativeJava = (NativeJava) funcVal.getValue();
      Value[] values = parameterValues.toArray(new Value[parameterValues.size()]);
      Object[] methodValues = new Object[values.length];
      int i = 0;
      for (Value val : parameterValues) {
        if (val == null){
          val = new Value();
        }
        methodValues[i++] = val.getValue();
      }
      Object returnValue = nativeJava.execute(this, (Object[]) methodValues);
      return returnValue == null ? new Value() : new Value(returnValue, false);
    }
  }

  public void allocateIfExists(String idName, Value value) throws ConstantReassignmentException {
    Value exists = globalSymbolTable.get(idName);
    if (exists != null) {
      globalSymbolTable.replace(idName, value);
    }
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
          boolean found = false;
          while(!callStack.isEmpty()) {
            Function currentFunction = callStack.pop();
            searchStack.push(currentFunction);
            Value v = currentFunction.getValue(idTerminal.getName());
            if (v != null) {
              currentFunction.allocate(idTerminal, value);
              found = true;
              break;
            }
          }
          while(!searchStack.isEmpty()){
            callStack.push(searchStack.pop());
          }
          if (!found){
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
        globalSymbolTable.replace(idName, value);
      } else {
        throw new ConstantReassignmentException(idName);
      }
    }
  }

  public void setAsGlobal(Terminal cur, Value value) throws ConstantReassignmentException {
    setAsGlobal(cur.getName(), value);
  }

  public Value getIdAsValue(Node id) {
    return new Value((Terminal)id.symbol(), false);
  }
  
  public boolean isset(Value value) {
    if (value == null) {
      return false;
    }
    Value foundValue = fetchInCallStack(value);
    if (foundValue == null) {
      String fetchIdName = ((Terminal) value.getValue()).getName();
      return globalSymbolTable.get(fetchIdName) != null;
    } else {
      return true;
    }
  }
  
  public Value fetch(Value value, Node curSeq) {
    if (value == null) {
      return null;
    }
    if (value.getValue() instanceof Terminal) {
      
        // Look in the functions first
        Value foundValue = fetchInCallStack(value);
        if (foundValue != null){
          return foundValue;
        }
        String fetchIdName = ((Terminal) value.getValue()).getName();
        Value val = globalSymbolTable.get(fetchIdName);
        if (val == null) {
          throw new RuntimeException("The ID '" + value + "' doesn't exist "
                  + "on line " + curSeq.getLine());
        }
        return new Value(val.getValue(), false);
    } else {
      return value;
    }
  }
  
  public Value fetchInCallStack(Value value) {
    Value valueFromFunction = null;
    boolean valueFound = false;
    Stack<Function> searchStack = new Stack<>();
    while(!callStack.isEmpty()) {
      Function currentFunction = callStack.pop();
      searchStack.push(currentFunction);
      valueFromFunction = currentFunction.getValue(value);
      if (valueFromFunction != null) {
        valueFound = true;
        break;
      }
    }
    while(!searchStack.isEmpty()){
      callStack.push(searchStack.pop());
    }
    if (valueFound) {
      return valueFromFunction;
    }
    return null;
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
