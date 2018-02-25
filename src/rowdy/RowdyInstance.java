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
import java.util.Stack;
import static rowdy.lang.RowdyGrammarConstants.*;


/**
 * Executes a parse tree given by a builder.
 *
 * @author Richard DeSilvey
 */
public class RowdyInstance {

  private BaseRowdyNode root;
  /**
   * Stores the name of each identifier or function
   */
  public final HashMap<String, Value> globalSymbolTable;
  /**
   * Keeps track of the level of loops the program is in.
   */
  public final Stack<BaseRowdyNode> activeLoops;
  /**
   * Keeps track of the functions currently being called.
   */
  public final Stack<Function> callStack;
  /**
   * Reference to the main function
  */
  private BaseRowdyNode main;
  private final List<String> runningList;
  private List<Value> programParamValues;
  private boolean firstTimeInitialization;

  public RowdyInstance() {
    this.root = null;
    main = null;
    callStack = new Stack<>();
    activeLoops = new Stack<>();
    globalSymbolTable = new HashMap<>();
    firstTimeInitialization = true;
    runningList = new ArrayList<>(50);
  }
  
  public void initialize(GRowdy builder) {
    root = (RowdyNode) builder.getProgram();
    compress(root);
  }
  
  /**
   * Optimization of a program tree, compresses the program eliminating 
   * redundant nodes in a tree. Expressions are mostly parts of a program
   * that need compression.
   * @param program The program being compressed.
   */
  public void compress(BaseRowdyNode program) {
    List<BaseRowdyNode> childrenNodes = program.getAll();
    BaseRowdyNode curNode, toSet;
    for (int i = 0; i < childrenNodes.size(); i++) {
      curNode = childrenNodes.get(i);
      compress(curNode);
      if (curNode.isCompressable()) {
        if (curNode.hasSymbols()) {
          int usefulCount = countUsefulChildren(curNode);
          if (usefulCount < 2) {
            toSet = curNode.getLeftMost();
            childrenNodes.remove(i);
            childrenNodes.add(i, toSet);
          }
        } else {
          childrenNodes.remove(i--);
        }
      }
    }
    BaseRowdyNode dup = program.getLeftMost();
    if (dup != null && dup.symbol().id() == program.symbol().id()) {
      int usefulCount = countUsefulChildren(program);
      if (usefulCount < 2) {
        program.setChildren(program.getLeftMost().getAll());
      }
    }
  }
  
  private int countUsefulChildren(BaseRowdyNode root) {
    int usefulCount = 0;
    List<BaseRowdyNode> children = root.getAll();
    BaseRowdyNode curNode;
    for (int i = 0; i < children.size(); i++) {
      curNode = children.get(i);
      if (curNode.hasSymbols() || curNode.symbol() instanceof Terminal) {
        usefulCount++;
      }
    }
    return usefulCount;
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
  public void declareGlobals(BaseRowdyNode parent) throws ConstantReassignmentException {
    BaseRowdyNode cur;
    ArrayList<BaseRowdyNode> children = parent.getAll();
    int currentID;
    
    for (int i = 0; i < children.size(); i++) {
      cur = children.get(i);
      currentID = cur.symbol().id();
      switch (currentID) {
        case ASSIGN_STMT:
          ((AssignStatement) cur).execute(null);
          break;
        case FUNCTION:
          Node modOpts = cur.get(NATIVE_FUNC_OPT);
          
          String functionName = ((Terminal) cur.get(ID).symbol()).getName();
          if (modOpts.hasSymbols()) {
            setAsGlobal(functionName, new Value());
          } else {
            if (functionName.equals("main")) {
              main = parent;
            }
            setAsGlobal(functionName, new Value(cur, true));
          }
          break;
        default:
          declareGlobals(cur);
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
  public void executeStmt(BaseRowdyNode parent, BaseRowdyNode seqControl) throws ConstantReassignmentException {
    BaseRowdyNode cur;
    if (parent == null) 
      throw new IllegalArgumentException("parent node is null");
    ArrayList<BaseRowdyNode> children = parent.getAll();
    for (int i = 0, curID; i < children.size(); i++) {
      cur = children.get(i);
      curID = cur.symbol().id();
      switch (curID) {
        case FUNCTION:
          String funcName = ((Terminal) cur.get(ID).symbol()).getName();
          Value funcVal = fetch(getIdAsValue(cur.get(ID)), cur);
          Value exitValue = executeFunc(funcName, funcVal, programParamValues);
          if (exitValue == null){
            exitValue = new Value(0, false);
          }
          System.exit(exitValue.valueToDouble().intValue());
        case ASSIGN_STMT:
        case LOOP_STMT:
        case BREAK_STMT:
          cur.execute(null);
          break;
        case IF_STMT:
          ((IfStatement) cur).execute(new Value(seqControl, false));
          break;
        
        case READ_STMT:
          ((ReadStatement) cur).execute(new Value(System.in, false));
          break;
        case FUNC_CALL:
          executeFunc(cur);
          break;
        case RETURN_STMT:
          ((ReturnStatement) cur).execute(new Value(seqControl, false));
          break;
        case PRINT_STMT:
          ((PrintStatement) cur).execute(new Value(System.out, false));
          break;
        default:
          if (seqControl != null) {
            if (seqControl.isSeqActive()) {
              executeStmt(cur, seqControl);
            }
          } else {
            executeStmt(cur, null);
          }
      }
    }
  }
  
  /**
   * Function calls with parameters are sent to this method.
   * @param cur
   * @return
   * @throws ConstantReassignmentException 
   */
  public Value executeFunc(BaseRowdyNode cur) throws ConstantReassignmentException {
    
    BaseRowdyNode idFuncRef = cur.get(ID_FUNC_REF);
    List<Value> parameterValues = new ArrayList<>();
    
    RowdyNode funcBodyExpr = (RowdyNode) idFuncRef.get(FUNC_BODY_EXPR);
    Expression paramValue = (Expression)funcBodyExpr.get(EXPRESSION);
    parameterValues.add(paramValue.execute());
    Node atomTailNode = funcBodyExpr.get(EXPR_LIST);
    
    while (atomTailNode.hasSymbols()) {
      paramValue = (Expression)atomTailNode.get(EXPRESSION);
      parameterValues.add(paramValue.execute());
      atomTailNode = atomTailNode.get(EXPR_LIST);
    }
    
    return executeFunc(cur, parameterValues);
  }
  
  /**
   * Determines if the function is native or not and executes it's code
   * with the given parameter values
   *
   * @param cur The function being executed
   * @param parameterValues The parameters passed to this function
   * @return The function's return value
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public Value executeFunc(Node cur, List<Value> parameterValues) throws ConstantReassignmentException {
    // 1. Collect parameters
    RowdyNode idFuncRef = (RowdyNode) cur.get(ID_FUNC_REF);
    String funcName = ((Terminal) idFuncRef.get(ID).symbol()).getName();
    Value funcVal = fetch(getIdAsValue(idFuncRef.get(ID)), cur);
    
    if (funcVal.getValue() instanceof RowdyNode) {
      
      return executeFunc(funcName, funcVal, parameterValues);
      
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
  
  /**
   * Executes the function, the name and it's code along with the parameters
   * are mapped out and pushed onto the call stack. This does not execute
   * native code.
   * @param funcName The name of the function to execute
   * @param funcVal The code of the function wrapped inside a Value
   * @param parameterValues The parameters to execute with
   * @return The value the function returns
   * @throws ConstantReassignmentException 
   */
  public Value executeFunc(String funcName, Value funcVal, List<Value> parameterValues) throws ConstantReassignmentException {
    BaseRowdyNode functionNode;
    if (runningList.contains(funcName)) {
      functionNode = ((BaseRowdyNode) funcVal.getValue()).copy();
    } else {
      functionNode = ((RowdyNode) funcVal.getValue());
      runningList.add(funcName);
    }
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
    Function function = new Function(funcName, params, functionNode.getLine());
    if (functionNode.symbol().id() == ANONYMOUS_FUNC || functionNode.get(DYNAMIC_OPT).hasSymbols()) {
      function.setAsDynamic();
    }
    callStack.push(function);
    // 4. Get and execute the stmt-list
    BaseRowdyNode funcStmtBlock = functionNode.get(FUNCTION_BODY).get(STMT_BLOCK);
    BaseRowdyNode stmtList = funcStmtBlock.get(STMT_LIST), seqControl = new RowdyNode(null, 0);
    seqControl.setSeqActive(true);
    executeStmt(stmtList, seqControl);
    // When finished, remove the function from the
    // call stack and free it's memory then return
    // it's value.
    function = callStack.pop();
    function.getSymbolTable().free();
    runningList.remove(funcName);
    return function.getReturnValue();
  }

  public void allocateIfExists(String idName, Value value) {
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
   * @param line The line number that allocation takes place
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public void allocate(Terminal idTerminal, Value value, int line) throws ConstantReassignmentException {
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
            Value v = currentFunction.getSymbolTable().getValue(idTerminal.getName());
            if (v != null) {
              currentFunction.getSymbolTable().allocate(idTerminal, value, line);
              found = true;
              break;
            } else if (!currentFunction.isDynamic()) {
              break;
            }
          }
          while(!searchStack.isEmpty()){
            callStack.push(searchStack.pop());
          }
          if (!found){
            Function currentFunction = callStack.peek();
            currentFunction.getSymbolTable().allocate(idTerminal, value, line);
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
        return val;
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
      valueFromFunction = currentFunction.getSymbolTable().getValue(value);
      if (valueFromFunction != null) {
        valueFound = true;
        break;
      } else if (!currentFunction.isDynamic()) {
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
