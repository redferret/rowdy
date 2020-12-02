package rowdy;

import growdy.Node;
import growdy.NonTerminal;
import growdy.Symbol;
import growdy.Terminal;
import rowdy.exceptions.MainNotFoundException;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.statement.*;
import rowdy.nodes.RowdyNode;
import rowdy.nodes.expression.AtomicId;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;
import static rowdy.BaseNode.instance;
import static rowdy.lang.RowdyGrammarConstants.*;
import static rowdy.BaseNode.UNSAFE;
import static rowdy.BaseNode.SAFE;


/**
 * Executes a parse tree given by a builder.
 *
 * @author Richard DeSilvey
 */
public class RowdyInstance {

  private BaseNode root;
  /**
   * Stores the name of each identifier or function globally
   */
  public final HashMap<String, Value> globalSymbolTable;
  
  /**
   * Keeps track of the functions currently being called.
   */
  public final Stack<Function> callStack;
  /**
   * Reference to the main function
  */
  private BaseNode main;
  
  private List<Value> programParamValues;
  private boolean firstTimeInitialization;
  private String nextImport;
  private InputStream inputStream;
  private OutputStream outputStream;
  private int currentLine;
  public static final int ATOMIC_SET = 0, ATOMIC_GET = 1;

  
  public RowdyInstance() {
    this.root = null;
    main = null;
    callStack = new Stack<>();
    globalSymbolTable = new HashMap<>();
    firstTimeInitialization = true;
    inputStream = System.in;
    outputStream = System.out;
  }
  
  public void initialize(RowdyNode program) {
    root = program;
  }
  
  /**
   * Optimization of a program tree, compresses the program eliminating 
   * redundant nodes in a tree reducing the number of calls to execute.
   * @param program The program being compressed.
   */
  public void compress(BaseNode program) {
    List<BaseNode> childrenNodes = program.getAll();
    BaseNode curNode;
    BaseNode replacement;
    if (program.symbol().id() == PARAMETERS) {
      int i = 0;
    }
    for (int i = 0; i < childrenNodes.size(); i++) {
      curNode = childrenNodes.get(i);
      compress(curNode);
      if (curNode.isCompressable()) {
        if (curNode.hasSymbols()) {
          int usefulCount = countUsefulChildren(curNode);
          if (usefulCount < 2) {
            replacement = curNode.getLeftMost();
            childrenNodes.remove(i);
            childrenNodes.add(i, replacement);
          }
        } else {
          childrenNodes.remove(i--);
        }
      }
    }
  }

  private int countUsefulChildren(BaseNode root) {
    int usefulCount = 0;
    List<BaseNode> children = root.getAll();
    BaseNode curNode;
    for (int i = 0; i < children.size(); i++) {
      curNode = children.get(i);
      if (curNode != null && curNode.hasSymbols()) {
        usefulCount++;
      }
    }
    return usefulCount;
  }
  
  /**
   * Some terminals in a program tree that won't serve any purpose are cut out
   * @param program 
   */
  public void removeTerminals(BaseNode program) {
    List<BaseNode> childrenNodes = program.getAll();
    BaseNode curNode;
    
    for (int i = 0; i < childrenNodes.size(); i++) {
      curNode = childrenNodes.get(i);
      removeTerminals(curNode);
      if (curNode.symbol() instanceof Terminal && !curNode.isCriticalTerminal()) {
        childrenNodes.remove(i--);
      } else if (curNode.symbol() instanceof NonTerminal && curNode.isEmpty() && !curNode.isCriticalTerminal()) {
        childrenNodes.remove(i--);
      }
    }
  }
  
  /**
   * A second pass at simplifying a program tree removing terminals and
   * nodes marked with reduce.
   * @param program 
   */
  public void reduce(BaseNode program) {
    List<BaseNode> childrenNodes = program.getAll();
    BaseNode curNode;
    BaseNode replacement;
    
    for (int i = 0; i < childrenNodes.size(); i++) {
      curNode = childrenNodes.get(i);
      reduce(curNode);
      if (curNode.canReduce()) {
        if (curNode.getAll().size() == 1) {
          replacement = curNode.getLeftMost();
          childrenNodes.remove(i);
          childrenNodes.add(i, replacement);
        }
      }
    }
  }
  
  private void checkForUnsafeFunctions(BaseNode program) {
    List<BaseNode> childrenNodes = program.getAll();
    BaseNode curNode;
    
    for (int i = 0; i < childrenNodes.size(); i++) {
      curNode = childrenNodes.get(i);
      checkForUnsafeFunctions(curNode);
      switch(curNode.symbol().id()) {
        case CLASS_DEF:
          markAllAsSafe(curNode);
          break;
        case FUNCTION_BODY:
          curNode.setObjectMutable(checkForSafety(curNode));
          break;
      }
    }
  }
  
  
  private int checkForSafety(BaseNode program) {
    List<BaseNode> childrenNodes = program.getAll();
    BaseNode curNode;
    
    for (int i = 0; i < childrenNodes.size(); i++) {
      curNode = childrenNodes.get(i);
      switch(curNode.symbol().id()) {
        case THIS_:
          return UNSAFE;
        default:
          return checkForSafety(curNode);
      }
    }
    return SAFE;
  }
  
  private void markAllAsSafe(BaseNode program) {
    List<BaseNode> childrenNodes = program.getAll();
    BaseNode curNode;
    
    for (int i = 0; i < childrenNodes.size(); i++) {
      curNode = childrenNodes.get(i);
      markAllAsSafe(curNode);
      switch(curNode.symbol().id()) {
        case FUNCTION_BODY:
          curNode.setObjectMutable(SAFE);
          break;
      }
    }
  }
  
  /**
   * Reduces the number of recursive calls on parameter nodes into a List
   * of expressions rather than a tree of expressions.
   * @param root
   * @throws ConstantReassignmentException 
   */
  public void simplifyParams(BaseNode root) throws ConstantReassignmentException {
    List<BaseNode> childrenNodes = root.getAll();
    BaseNode curNode;
    for (int i = 0; i < childrenNodes.size(); i++) {
      curNode = childrenNodes.get(i);
      if (curNode == null) continue;
      simplifyParams(curNode);
      String paramsId;
      switch (curNode.symbol().id()) {
        case PRIVATE_SCOPE:
        case FUNCTION_BODY:
          List<String> paramsList = new ArrayList<>();
          BaseNode funcParams = curNode.get(PARAMETERS);
          if (funcParams != null && funcParams.hasSymbols()) {
            Node id = funcParams.get(ID);
            String idValueAsString = getIdAsValue(id).toString();
            paramsList.add(idValueAsString);
            
            BaseNode paramsTailNode = funcParams.get(PARAMS_TAIL);
            while (paramsTailNode != null && paramsTailNode.hasSymbols()) {
              paramsList.add(getIdAsValue(paramsTailNode.get(ID)).toString());
              paramsTailNode = paramsTailNode.get(PARAMS_TAIL);
            }
          }
          if (!paramsList.isEmpty()) {
            paramsId = "func-params " + curNode.getLine() + ThreadLocalRandom.current().nextInt();
            buildParameterNodeForParent(funcParams, paramsId, curNode.getLine());
            funcParams.setChildren(funcParams.get(PARAMETERS).getAll());
            setAsGlobal(paramsId, new Value(paramsList, true));
          } else {
            if (funcParams != null) {
              BaseNode parameters = new RowdyNode(new NonTerminal("parameters", PARAMETERS), curNode.getLine());
              funcParams.add(parameters);
            }
          }
          break;
        case PRINT_STMT:
        case CONCAT_EXPR:
        case FUNC_CALL:
        case NEW_OBJ:
          List<BaseNode> params = new ArrayList<>();
          BaseNode idNode = curNode.get(ID, false);
          if (idNode != null) {
            paramsId = getIdAsValue(idNode).toString();
          } else {
            paramsId = curNode.symbol().getSymbolAsString();
          }
          paramsId += "-params " + curNode.getLine() + ThreadLocalRandom.current().nextInt();
          
          BaseNode parentNode = curNode.get(FUNC_PARAMS, false);
          if (parentNode == null) {
            parentNode = curNode;
          }
          BaseNode param = parentNode.get(EXPRESSION);
          if (param != null && param.hasSymbols()) {
            removeTerminals(param);
            reduce(param);
            checkForUnsafeFunctions(root);
            params.add(param);
          }
          BaseNode atomTailNode = parentNode.get(EXPR_LIST);
          while (atomTailNode != null && atomTailNode.hasSymbols()) {
            param = atomTailNode.get(EXPRESSION);
            if (param != null) {
              removeTerminals(param);
              reduce(param);
              checkForUnsafeFunctions(root);
              params.add(param);
            }
            atomTailNode = atomTailNode.get(EXPR_LIST);
          }
          if (!params.isEmpty()) {
            buildParameterNodeForParent(parentNode, paramsId, curNode.getLine());
            setAsGlobal(paramsId, new Value(params, true));
          }
      }
    }
  }

  public void buildParameterNodeForParent(BaseNode parentNode, String paramsId, int line) {
    BaseNode parameters = new RowdyNode(new NonTerminal("parameters", PARAMETERS), line);
    BaseNode atomicId = new AtomicId(new NonTerminal("atomic-id", ATOMIC_ID), line);
    BaseNode id = new RowdyNode(new Terminal("id", ID, paramsId), line);
    id.setAsCriticalTerminal();
    
    atomicId.add(id);
    parameters.add(atomicId);
    parentNode.getAll().clear();
    parentNode.add(parameters);
  }

  /**
   * Pulls out all meta data in a program tree an allocates each constant
   * into the global symbol table
   * @param parent
   * @throws ConstantReassignmentException 
   */
  public void extractConstants(BaseNode parent) throws ConstantReassignmentException {
    List<BaseNode> childrenNodes = parent.getAll();
    BaseNode curNode;
    for (int i = 0; i < childrenNodes.size(); i++) {
      curNode = childrenNodes.get(i);
      if (curNode == null) continue;
      extractConstants(curNode);
      switch (curNode.symbol().id()) {
        case ATOMIC_CONST:
          int line = parent.getLine();
          String paramsId = "const-" +line+ ThreadLocalRandom.current().nextInt();
          BaseNode atomicId = new AtomicId(new NonTerminal("atomic-id", ATOMIC_ID), line);
          BaseNode id = new RowdyNode(new Terminal("id", ID, paramsId), line);
          id.setAsCriticalTerminal();
          
          atomicId.add(id);
          childrenNodes.remove(i);
          childrenNodes.add(i, atomicId);
          String baseValue = curNode.get(CONSTANT).toString().replaceAll("\"", "");
          setAsGlobal(paramsId, new Value(baseValue, true));
      }
    }
  }

  /**
   * Reduces the size of the program tree to decrease the number of recursive
   * calls.
   * @param root
   * @throws ConstantReassignmentException 
   */
  public void optimizeProgram(BaseNode root) throws ConstantReassignmentException {
    compress(root);
    extractConstants(root);
    simplifyParams(root);
    removeTerminals(root);
    reduce(root);
    checkForUnsafeFunctions(root);
  }
  
  /**
   * Only call this method if the program has stopped executing.
   */
  public void dumpCallStack() {
    if (!callStack.isEmpty()){
      System.err.println("Exception on line " + currentLine);
      System.err.print("Call Stack:\n");
      callStack.forEach(function -> {
        System.err.println("->" + function.getName() + ": line " + 
                function.getLineCalledOn());
      });
    }
  }

  public void executeLine() throws ConstantReassignmentException {
    declareSystemConstants();
    optimizeProgram(root);
    executeStmt(root, null);
  }
  
  public void declareGlobals() throws ConstantReassignmentException {
    this.declareGlobals(root);
  }
  
  /**
   * Runs the program
   *
   * @param programParams The program parameters
   * @throws MainNotFoundException
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public void execute(List<Value> programParams) throws MainNotFoundException, ConstantReassignmentException {
    this.programParamValues = programParams;
    declareSystemConstants();
    declareGlobals();
    if (main == null){
      throw new MainNotFoundException("main method not found");
    }
    Value exitValue = executeFunc("main", main.get(FUNCTION), programParamValues);
    if (exitValue == null){
      exitValue = new Value(0, false);
    }
    System.exit(exitValue.getValue() == null ? 0 : (int) exitValue.getValue());
  }
  
  public void declareSystemConstants() throws ConstantReassignmentException {
    if (firstTimeInitialization) {
      setAsGlobal("true", new Value(true, true));
      setAsGlobal("false", new Value(false, true));
      setAsGlobal("null", new Value(null, true));
      setAsGlobal("char", new Value(' ', true));
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
  public void declareGlobals(BaseNode parent) throws ConstantReassignmentException {
    BaseNode cur;
    ArrayList<BaseNode> children = parent.getAll();
    int currentID;
    
    for (int i = 0; i < children.size(); i++) {
      cur = children.get(i);
      currentID = cur.symbol().id();
      switch (currentID) {
        case ASSIGN_STMT:
          ((AssignStatement) cur).execute();
          break;
        case FUNCTION:
          Node options = cur.get(FUNC_OPTS);
          Node asNativeFunction = null;
          
          if (options != null && options.hasSymbols()) {
            asNativeFunction = options.get(NATIVE_FUNC_OPT);
          }
          
          String functionName = cur.get(ID).symbol().toString();
          if (asNativeFunction != null && asNativeFunction.hasSymbols()) {
            setAsGlobal(functionName, new Value());
          } else {
            if (functionName.equals("main") && main == null) {
              main = parent;
            } else if (functionName.equals("main") && main != null) {
              break;
            }
            setAsGlobal(functionName, new Value(cur, true));
          }
          break;
        case CLASS_DEF:
          RowdyClass rowdyClass = new RowdyClass(cur);
          setAsGlobal(rowdyClass.getObjectName(), new Value(rowdyClass, true));
          break;
        default:
          declareGlobals(cur);
      }
    }
  }

  /**
   * Executes statements recursively
   *
   * @param parent The start of execution
   * @param seqControl The node that flags sequence control to stop executing on
   * the parent. This lets break and return statements drop the sequence and not
   * execute any remaining statements until sequence control is given back to
   * the original caller.
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public void executeStmt(BaseNode parent, BaseNode seqControl) throws ConstantReassignmentException {
    
    BaseNode currentNode;
    if (parent == null) 
      return;
    ArrayList<BaseNode> programNodes = parent.getAll();
    
    for (int i = 0, curNodeId; i < programNodes.size(); i++) {
      
      currentNode = programNodes.get(i);
      curNodeId = currentNode.symbol().id();
      
      if (currentNode.symbol().id() != STMT_LIST) {
        currentLine = currentNode.getLine();
      }
      
      switch (curNodeId) {
        case ASSIGN_STMT:
        case LOOP_STMT:
        case WHILE_LOOP:
        case BREAK_STMT:
          currentNode.execute();
          break;
        case IF_STMT:
          ((IfStatement) currentNode).execute(seqControl);
          break;
        case READ_STMT:
          ((ReadStatement) currentNode).execute(inputStream);
          break;
        case FUNC_CALL:
          executeFunc(currentNode);
          break;
        case RETURN_STMT:
          ((ReturnStatement) currentNode).execute(seqControl);
          break;
        case PRINT_STMT:
          ((PrintStatement) currentNode).execute(outputStream);
          break;
        case SINGLE_IMPORT:
          Node importConstant = currentNode.get(CONSTANT, false);
          if (importConstant != null) {
            nextImport = ((Terminal) importConstant.symbol()).getValue().replaceAll("\\.", "/").replaceAll("\"", "");
          }
        default:
          if (seqControl != null) {
            if (seqControl.isSeqActive()) {
              executeStmt(currentNode, seqControl);
            }
          } else {
            executeStmt(currentNode, null);
          }
      }
    }
  }
  
  public String getNextImport() {
    String temp = nextImport;
    nextImport = null;
    return temp;
  }
  
  /**
   * Function calls with optimized parameters are executed.
   * @param root
   * @return
   * @throws ConstantReassignmentException 
   */
  public Value executeFunc(BaseNode root) throws ConstantReassignmentException {
    
    List<Value> parameterValues = new ArrayList<>();
    
    BaseNode funcBodyExpr = root.get(FUNC_PARAMS);
    if (funcBodyExpr != null) {
      Value paramsValue = (Value) funcBodyExpr.execute(new Value(new ArrayList<>(), false));
      List<BaseNode> params = (List<BaseNode>) paramsValue.getValue();

      params.forEach((expression) -> {
        Value v = (Value) expression.execute();
        v.setAsConstant(false);
        parameterValues.add(v);
      });
    }
    return executeFunc(root, parameterValues);
  }
  
  /**
   * Determines if the function is native or not and executes it's code
   * with the given parameter values. Additionally decides if there's a 
   * reference involved with calling a function using the dot operator.
   *
   * @param functionCode The function being executed
   * @param parameterValues The parameters passed to this function
   * @return The function's return value
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public Value executeFunc(BaseNode functionCode, List<Value> parameterValues) throws ConstantReassignmentException {
    BaseNode idFuncRef = functionCode.get(ATOMIC_ID).get(REF_ACCESS);
    BaseNode idNode = null;
    if (idFuncRef != null && idFuncRef.hasSymbols()) {
      switch(idFuncRef.getLeftMost().symbol().id()) {
        case ID_:
          idNode = idFuncRef.get(ID_).get(ID);
          break;
      }
    } else {
      idNode = functionCode.get(ID);
    }
    String funcName = "";
    Value funcVal;
    if (idNode == null) {
      Function top = this.callStack.peek();
      if (top.isMemberFunction()) {
        funcVal = new Value(this.callStack.peek().getClassObject(), false);
      } else {
        throw new RuntimeException("Reference to object with unknown context on line " + functionCode.getLine());
      }
    } else {
      funcVal = fetch(getIdAsValue(idNode), functionCode);
      funcName = idNode.symbol().toString();
    }
    Object value = funcVal.getValue();
    if (value instanceof BaseNode) {
      return executeFunc(funcName, (BaseNode) value, parameterValues);
    } else if (value instanceof NativeJavaCode){
      NativeJavaCode nativeJava = (NativeJavaCode) value;
      if (nativeJava == null) {
        throw new IllegalArgumentException("No Native Java found");
      }
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
    } else if (value instanceof RowdyObject){
      return objectFuncReference(funcVal, functionCode, parameterValues);
    } else {
      if (value instanceof ArrayList || value instanceof HashMap) {
        // Because the funcVal is mutated when RAMAccess is executed,
        // there must be a temp storage for the original value in memory.
        // This manupulation is neccessary for performing multiple dot operations
        // on both objects and objects stored in arrays.
        Object tempStorage = funcVal.getValue();
        
        Value returnVal = null;
        try {
          arrayAccess(functionCode.get(ATOMIC_ID).get(ARRAY_ACCESS), funcVal, funcVal, funcName, ATOMIC_GET);
          if (funcVal.getValue() instanceof RowdyObject) {
            returnVal = objectFuncReference(funcVal, functionCode, parameterValues);
          } else {
            returnVal = executeFunc("func-call", (BaseNode) funcVal.getValue(), parameterValues);
          }
        } finally {
          funcVal.setValue(tempStorage);
        }
        
        return returnVal;
      }
      throw new RuntimeException("Undefined function call on line " + functionCode.getLine());
    }
  }

  private Value objectFuncReference(Value funcVal, BaseNode functionCode, List<Value> parameterValues) throws ConstantReassignmentException {
    RowdyObject rowdyObject = (RowdyObject) funcVal.getValue();
    SymbolTable instanceTable = rowdyObject.getSymbolTable();
    
    BaseNode refAccess = functionCode.get(ATOMIC_ID).get(REF_ACCESS);
    BaseNode id = refAccess.getLeftMost().get(ID);
    BaseNode objAtomic = functionCode.get(ATOMIC_ID).get(DOT_ATOMIC);
    
    String memberFuncName = "unknown";
    if (id != null) {
      memberFuncName = id.symbol().toString();
    }
    
    if (objAtomic != null && objAtomic.hasSymbols()) {
      instanceTable = (SymbolTable) instance.atomicReference(objAtomic, rowdyObject.getSymbolTable(), REF_ACCESS);
      id = (BaseNode) instance.atomicReference(objAtomic, rowdyObject.getSymbolTable(), ATOMIC_ID);
      memberFuncName = id.get(REF_ACCESS).getLeftMost().get(ID).symbol().toString();
    }
    
    funcVal = (Value) functionCode.get(ATOMIC_ID).execute();
    Value returnVal = executeFunc(memberFuncName, (BaseNode) funcVal.getValue(), parameterValues, (RowdyObject) instanceTable.getInstanceObject());
    
    BaseNode arrayPart = functionCode.get(ARRAY_PART);
    if (arrayPart != null && arrayPart.hasSymbols()) {
      // TODO: Incomplete use case for a function call '$f()[0]'
    }
    return returnVal;
  }
  
  
  /**
   * Determines if the function is a member function or not
   * @param funcName The name of the function
   * @param functionNode the function's code
   * @param parameterValues the list of evaluated parameters 
   * @return
   * @throws ConstantReassignmentException 
   */
  public Value executeFunc(String funcName, BaseNode functionNode, List<Value> parameterValues) throws ConstantReassignmentException {
    SymbolTable mostRecentContext = topLevelContext();
    if (mostRecentContext != null && mostRecentContext.getInstanceObject() instanceof RowdyObject) {
      return executeFunc(funcName, functionNode, parameterValues, (RowdyObject) mostRecentContext.getInstanceObject());
    } else {
      return executeFunc(funcName, functionNode, parameterValues, null);
    }
  }
  
  /**
   * Executes functions that could be a member or non-member function 
   * depending if a RowdyObject is passed in, the code for the function should
   * be present and known.
   * @param funcName The name of the function to execute
   * @param functionNode The code of the function
   * @param parameterValues The parameters to execute with
   * @param parent The object the function belongs to, can be null
   * @return The value the function returns
   * @throws ConstantReassignmentException 
   */
  public Value executeFunc(String funcName, BaseNode functionNode, List<Value> parameterValues, RowdyObject parent) throws ConstantReassignmentException {

    // 1. Get the formal parameters
    BaseNode functionBody = functionNode.get(FUNCTION_BODY);
    BaseNode parameters = functionBody.get(PARAMETERS);
    
    List<String> formalParams = new ArrayList<>();
    
    if (parameters != null) {
      Value paramValue = (Value) parameters.execute(new Value(new ArrayList<>(), false));
      formalParams = (List<String>) paramValue.getValue();
    }
    
    // 2. Copy actual parameters to formal parameters
    HashMap<String, Value> params = new HashMap<>();
    String paramName;
    if (parameterValues.isEmpty() && !formalParams.isEmpty()) {
      for (int p = 0; p < formalParams.size(); p++) {
        paramName = formalParams.get(p);
        params.put(paramName, new Value(null, false));
      }
    } else if (parameterValues.size() < formalParams.size()) {
      for (int p = 0; p < parameterValues.size(); p++) {
        paramName = formalParams.get(p);
        params.put(paramName, parameterValues.get(p));
      }
      int formalDiff = parameterValues.size();
      for (int p = formalDiff; p < formalParams.size(); p++) {
        paramName = formalParams.get(p);
        params.put(paramName, new Value(null, false));
      }
    } else if (parameterValues.size() > formalParams.size() || 
            parameterValues.size() == formalParams.size()) {
      for (int p = 0; p < formalParams.size(); p++) {
        paramName = formalParams.get(p);
        params.put(paramName, parameterValues.get(p));
      }
    }
    
    Function function = new Function(funcName, params, functionNode.getLine());
    function.setClassObject(parent);
    
    BaseNode funcOpts = functionNode.get(FUNC_OPTS);
    if (functionNode.symbol().id() == ANONYMOUS_FUNC) {
      function.setAsDynamic();
    } else if (funcOpts != null) {
      BaseNode option = funcOpts.getLeftMost();
      if (option != null) {
        switch(option.symbol().id()) {
          case DYNAMIC_OPT:
            if (option.hasSymbols()) {
              function.setAsDynamic();
            }
            break;
        }
      }
    }
    
    // 3. Get the stmt-list and determine if the function is safe
    BaseNode funcBody = functionNode.get(FUNCTION_BODY);
    if (!funcBody.isObjectSafe() && parent != null) {
      throw new RuntimeException("Attempting to execute an unsafe function in a "
              + "restricted context '" + parent.getNameOfObject() + "'" + " on line " + functionNode.getLine());
    }
    
    BaseNode privateScope = funcBody.get(PRIVATE_SCOPE);
    
    if (privateScope != null && privateScope.hasSymbols()) {
      Value privateValue = (Value) privateScope.execute(new Value(new ArrayList<>(), false));
      List<String> privateValues = (List<String>) privateValue.getValue();

      privateValues.forEach((param) -> {
        params.put(param, new Value(null));
      });
    }
    
    BaseNode stmtList = funcBody.get(STMT_LIST), seqControl = new RowdyNode(null, 0);
    seqControl.setSeqActive(true);
    
    // 4. Push the function onto the call stack
    callStack.push(function);
    executeStmt(stmtList, seqControl);
    // When finished, remove the function from the
    // call stack and free it's memory then return
    // it's value.
    function = callStack.pop();
    function.getSymbolTable().free();
    return function.getReturnValue();
  }

  public void allocateIfExists(String idName, Value value) {
    Value exists = globalSymbolTable.get(idName);
    if (exists != null) {
      globalSymbolTable.replace(idName, value);
    }
  }

  /**
   * If the variable is not a global variable it will allocate the variable to
   * the current function if it doesn't exist. If the variable exists the value
   * passed in will overwrite the current value. This is a soft allocation
   * unlike the method RAMAccess which performs a more complex allocation using
   * dot operators.
   *
   * @param idName The variable to allocate or change
   * @param value The Value being allocated or changed
   * @param line The line number that allocation takes place
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public void allocate(String idName, Value value, int line) throws ConstantReassignmentException {
    if (callStack.isEmpty()) {
      setAsGlobal(idName, value);
    } else {
        Function[] functions = callStack.toArray(new Function[callStack.size()]);
        boolean found = false;
        for (int i = functions.length - 1; i >= 0; i--) {
          Function currentFunction = functions[i];
          Value v = currentFunction.getSymbolTable().getValue(idName);
          if (v != null) {
            currentFunction.getSymbolTable().allocate(idName, value, line, false);
            found = true;
            break;
          } else if (!currentFunction.isDynamic()) {
            break;
          }
        }
        if (!found){
          Value exists = globalSymbolTable.get(idName);
          if (exists == null) {
            Function currentFunction = callStack.peek();
            currentFunction.getSymbolTable().allocate(idName, value, line, false);
          } else {
            setAsGlobal(idName, value);
          }
        }
    }
  }

  /**
   * allocates/sets memory using the name of the symbol and a given value
   * in the global symbol table only
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

  public Value getIdAsValue(Node id) {
    return new Value((Terminal)id.symbol(), false);
  }
  
  public boolean isset(Value value) {
    if (value == null) {
      return false;
    }
    Value foundValue = fetchInCallStack(value);
    if (foundValue == null) {
      String fetchIdName = ((Terminal) value.getValue()).getValue();
      foundValue = globalSymbolTable.get(fetchIdName);
      return (foundValue != null && foundValue.getValue() != null);
    } else {
      return (foundValue != null && foundValue.getValue() != null);
    }
  }

  public Value fetch(Value value, Node curSeq) {
    return fetch(value, curSeq, true);
  }
  
  /**
   * Performs a fetch of a variable in the call stack or returns the value
   * passed in as is if the value is not a Terminal node. Fetches a variable in
   * the call stack on functions and halts search on static scopes.
   * @param value
   * @param curSeq
   * @param throwNotFoundException
   * @return 
   */
  public Value fetch(Value value, Node curSeq, boolean throwNotFoundException) {
    if (value == null) {
      return null;
    }
    if (value.getValue() instanceof Terminal) {
        Value foundValue = fetchInCallStack(value);
        if (foundValue != null){
          return foundValue;
        }
        String fetchIdName = ((Terminal) value.getValue()).getValue();
        Value val = globalSymbolTable.get(fetchIdName);
        if (val == null && throwNotFoundException) {
          throw new RuntimeException("The ID '" + value + "' doesn't exist "
                  + "on line " + curSeq.getLine());
        }
        return val;
    } else {
      return value;
    }
  }
  
  public void setInputStream(InputStream stream) {
    this.inputStream = stream;
  }
  
  public void setOutputStream(OutputStream stream) {
    this.outputStream = stream;
  }
  
  /**
   * Fetches a variable in the call stack on functions and halts search on
   * static scopes.
   * @param value
   * @return 
   */
  public Value fetchInCallStack(Value value) {
    Value valueFromFunction = null;
    boolean valueFound = false;
    
    Function[] functions = callStack.toArray(new Function[callStack.size()]);
    
    for (int i = functions.length - 1; i >= 0; i--) {
      Function currentFunction = functions[i];
      valueFromFunction = currentFunction.getSymbolTable().getValue(value);
      if (valueFromFunction != null) {
        valueFound = true;
        break;
      } else if (!currentFunction.isDynamic()) {
        break;
      }
    }
    
    if (valueFound) {
      return valueFromFunction;
    }
    return null;
  }

  public Value RAMAccess(BaseNode root, Value value, int action) {
    return RowdyInstance.this.RAMAccess(root, value, action, true);
  } 
  
  /**
   * Performs a dot operation on a variable based on the context and
   * scope. The action is the action of either ATOMIC_GET or ATOMIC_SET.
   * <code>RAMAccess(root, new Value(), ATOMIC_GET)</code> will return the
   * value to the caller and also wrapped in <code>new Value()</code>.
   *
   * If the method is enabled for setting a value, the Value passed in is what
   * will be allocated to the variable, 
   * <code>RAMAccess(root, new Value(10, false), ATOMIC_SET)</code>
   * will assign the value 10 based on the code given to this method.
   * that is being set as well.
   *
   * @param root The code that will be used to access a variable
   * @param value The value used for getting or setting
   * @param action The action this method should perform
   * @param throwNotFoundException Throws an exception if the ID is not found
   * @return The value that is set or fetched from a variable
   */
  public Value RAMAccess(BaseNode root, Value value, int action, boolean throwNotFoundException) {
    BaseNode id;
    BaseNode refAccess = root.get(REF_ACCESS);
    BaseNode arrayAccess = null;
    BaseNode objAtomic;
    SymbolTable context = null;
    
    Value searchValue;
    String idName;
    
    if (refAccess != null && refAccess.hasSymbols()) {
      switch(refAccess.getLeftMost().symbol().id()) {
        case ID_:
          id = refAccess.getLeftMost().get(ID);
          objAtomic = root.get(DOT_ATOMIC);
          if (objAtomic != null && objAtomic.hasSymbols()) {
            Value valueContext = instance.getIdFromTopLevelContext(id);
            Value refValue = fetch(new Value(id.symbol()), root, throwNotFoundException);
            RowdyObject objectRef;
            if (refValue.getValue() instanceof RowdyObject) {
              if (valueContext == null) {
                objectRef = (RowdyObject) fetch(new Value(id.symbol()), root, throwNotFoundException).getValue();
              } else {
                objectRef = (RowdyObject) valueContext.getValue();
              }
              context = (SymbolTable) atomicReference(objAtomic, objectRef.getSymbolTable(), REF_ACCESS);
              id = (BaseNode) atomicReference(objAtomic, objectRef.getSymbolTable(), ATOMIC_ID);
              arrayAccess = id.get(ARRAY_ACCESS);
            } else if (refValue.getValue() instanceof List || refValue.getValue() instanceof HashMap) {
              arrayAccess = root.get(ARRAY_ACCESS);
              BaseNode idNode = refAccess.getLeftMost().get(ID);
              idName = idNode.symbol().toString();
              
              Value objectRefValue = new Value();
              arrayAccess(arrayAccess, refValue, objectRefValue, idName, ATOMIC_GET);
              objectRef = (RowdyObject) objectRefValue.getValue();
              context = (SymbolTable) atomicReference(objAtomic, objectRef.getSymbolTable(), REF_ACCESS);
              id = (BaseNode) atomicReference(objAtomic, objectRef.getSymbolTable(), ATOMIC_ID);
              arrayAccess = null;
            } else {
              throw new RuntimeException("Unsupported " + root.getLine());
            }
            refAccess = id.get(REF_ACCESS);
            BaseNode idNode = refAccess.getLeftMost().get(ID);
            idName = idNode.symbol().toString();
            searchValue = new Value(idNode.symbol());
          } else {
            arrayAccess = root.get(ARRAY_ACCESS);
            searchValue = new Value(id.symbol());
            idName = id.symbol().toString();
          }
          break;
        case THIS_:
          objAtomic = root.get(DOT_ATOMIC);
          
          if (objAtomic == null) {
            value.setValue(topLevelContext().getInstanceObject());
            return value;
          } else {
            context = (SymbolTable) atomicReference(objAtomic, topLevelContext(), REF_ACCESS);
            id = (BaseNode) atomicReference(objAtomic, topLevelContext(), ATOMIC_ID);
          }
          
          refAccess = id.get(REF_ACCESS);
          BaseNode idNode = refAccess.getLeftMost().get(ID);
          idName = idNode.symbol().toString();
          arrayAccess = id.get(ARRAY_ACCESS);
          searchValue = new Value(idNode.symbol());
          break;
        default:
          throw new RuntimeException("Unable to determine context on line " + root.getLine());
      }
      
    } else {
      id = root.get(ID);
      searchValue = new Value(id.symbol());
      idName = id.symbol().toString();
    }
    
    try {
      if (context != null && context instanceof SymbolTable) {
        SymbolTable contextTable = (SymbolTable) context;
        
        if (arrayAccess != null && arrayAccess.hasSymbols()) {
          Value objs = contextTable.getValue(idName);
          arrayAccess(arrayAccess, objs, value, idName, action);
        } else {
          switch(action) {
            case ATOMIC_SET:
              contextTable.allocate(idName, value, root.getLine(), false);
              break;
            case ATOMIC_GET:
              Value returnValue = contextTable.getValue(idName);
              if (returnValue == null && throwNotFoundException) {
                throw new RuntimeException("The ID '" + idName + "' doesn't exist "
                        + "on line " + root.getLine());
              } else if (returnValue == null && !throwNotFoundException) {
                value.setValue(null);
              }else {
                value.setValue(returnValue.getValue());
              }
          }
          
        }
      } else {
        if (arrayAccess != null && arrayAccess.hasSymbols()) {
          arrayAccess(arrayAccess, fetch(searchValue, root, throwNotFoundException), value, idName, action);
        } else {
          switch(action) {
            case ATOMIC_SET:
              allocate(idName, value, root.getLine());
              break;
            case ATOMIC_GET:
              Value returnValue = fetch(searchValue, root, throwNotFoundException);
              if (returnValue == null) {
                return null;
              }
              value.setValue(returnValue.getValue());
          }
        }
      }
    } catch (ConstantReassignmentException ex) {
      throw new RuntimeException(ex);
    }
    
    return value;
  }
  
  /**
   * Searches using the supplied id node to look a value up in the current
   * context and the scope of the call stack.
   * If the function on the top of the call stack is a member function that
   * object's context (symbol table) is used to look the given id up
   * If there is no object being referenced
   * @param id
   * @return 
   */
  public Value getIdFromTopLevelContext(BaseNode id) {
    Value val;
    SymbolTable table = topLevelContext();
    if (table == null) {
      val = fetchInCallStack(new Value(id.symbol()));
    } else {
      val = table.getValue(id.symbol().toString());
    }
    return val;
  }
  
  
  /**
   * Returns the context based on the current function in the call stack.
   * If the current executing function is a member function the object's
   * symbol table is returned, if the function is dynamic then null is returned,
   * otherwise the current function's symbol table is returned.
   * @return 
   */
  public SymbolTable topLevelContext() {
    if (callStack.isEmpty()) {
      return null;
    }
    Function curFunction = callStack.peek();

    if (curFunction.isMemberFunction()) {
      return curFunction.getClassObject().getSymbolTable();
    } else {
      return curFunction.getSymbolTable();
    }
  }
  
  /**
   * Performs dot operations recursively returning either an Atomic_Id node or
   * the symbol table.
   * Actions that can be taken are ATOMIC_ID or REF_ACCESS.
   * ATOMIC_ID action returns the highest level atomic id found in the root.
   * REF_ACCESS action returns the symbol table of the object in the root.
   * Given the code <code>a.b.c</code> with the action ATOMIC_ID the 
   * node returned is <code>c</code>. With the action REF_ACCESS the symbol
   * table for the object referenced with <code>c</code> is returned. The number
   * of recursive calls on this code is two, since there are two dot operations.
   * The method should be primed with the root code and the current symbol table
   * that is being accessed along with the given action.
   * @param root The code that is being executed
   * @param context The current symbol table
   * @param action The type of object that should be returned
   * @return 
   */
  public Object atomicReference(BaseNode root, SymbolTable context, int action) {
    BaseNode atomicId = root.get(ATOMIC_ID);
    BaseNode objAtomic = atomicId.get(DOT_ATOMIC);
    BaseNode refAccess = atomicId.get(REF_ACCESS);
    BaseNode idNode = refAccess.get(ID_).get(ID);
    BaseNode arrayAccess = atomicId.get(ARRAY_ACCESS);
    String idName = idNode.symbol().toString();

    if (objAtomic != null) {
      Value contextValue = context.getValue(idName);
      RowdyObject objectRef;
      if (contextValue.getValue() instanceof RowdyObject) {
        objectRef = (RowdyObject) contextValue.getValue();
      } else {
        if (arrayAccess != null && arrayAccess.hasSymbols()) {
          Value arrMember = new Value();
          arrayAccess(arrayAccess, contextValue, arrMember, idName, ATOMIC_GET);
          objectRef = (RowdyObject) arrMember.getValue();
        } else {
          throw new RuntimeException("Invalid object reference on line " + root.getLine());
        }
      }
      if (objectRef != null) {
        return atomicReference(objAtomic, objectRef.getSymbolTable(), action);
      } else {
        throw new RuntimeException("Object Reference not found on line " + root.getLine());
      }
    } else {
      switch(action) {
        case ATOMIC_ID:
          return atomicId;
        case REF_ACCESS:
          return context;
        default:
          return null;
      }
    }
  }
  
  /**
   * Performs an access on a map or list with the given array access code
   * on the map or list wrapped in a Value which is usually fetched from RAM.
   * The two actions that be be done are ATOMIC_GET or ATOMIC_SET. 
   * If the method is to perform an ATOMIC_GET the value located in the array
   * or map is wrapped in the value object passed into this method. Otherwise
   * if the method is to perform an ATOMIC_SET the value passed into the method
   * is the value that will be set in the array or map that is given.
   * @param arrayDef The code to be executed
   * @param arrayType The array or map that is being worked on
   * @param value The value wrapper that is used to get or set 
   * @param arrayName The name of the array
   * @param action The action the method should perform, either an ATOMIC_GET
   * or an ATOMIC_SET operation.
   */
  public void arrayAccess(BaseNode arrayDef, Value arrayType, Value value, String arrayName, int action) {
    final List indexes = new ArrayList();
    BaseNode arrayAccess = (BaseNode) arrayDef.getLeftMost();
    arrayDef = arrayDef.get(ARRAY_ACCESS);
    Value arrayAccessValue = (Value) arrayAccess.execute();
    indexes.add(arrayAccessValue.getValue());
    if (arrayType.getValue() instanceof List) {
      List elements = (List) arrayType.getValue();

      while(arrayDef.hasSymbols()) {
        Object element = elements.get((int) arrayAccessValue.getValue());
        if (element instanceof List) {
          elements = (List) element;
          arrayAccessValue = (Value) arrayDef.getLeftMost().execute();
          indexes.add(arrayAccessValue.getValue());
          arrayDef = arrayDef.get(ARRAY_ACCESS);
        } else {
          String indexStr = "";
          for (int i = 0; i < indexes.size(); i++) {
            indexStr += "["+indexes.get(i)+"]";
          }
          throw new RuntimeException("Attempting to access a non-array '" 
                  + arrayName + "' indexed " + indexStr + " on line " + arrayDef.getLine());
        }
      }
      switch (action) {
        case ATOMIC_GET:
          value.setValue(elements.get((int) arrayAccessValue.getValue()));
          break;
        case ATOMIC_SET:
          elements.set((int) arrayAccessValue.getValue(), value.getValue());
          break;
      }
      
    } else if (arrayType.getValue() instanceof HashMap) {
      HashMap map = (HashMap) arrayType.getValue();
      switch (action) {
        case ATOMIC_GET:
          value.setValue(map.get(arrayAccessValue.getValue()));
          break;
        case ATOMIC_SET:
          map.put(arrayAccessValue.getValue(), value.getValue());
          break;
      }
    } else {
      throw new RuntimeException("Attempting to access a non-array '" + arrayName + "' on line " + arrayDef.getLine());
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
      Node child = children.get(i);
      if (child.symbol() instanceof NonTerminal) {
        System.out.println(child.symbol());
        print(child);
      } else {
        System.out.println(child.symbol());
      }
    }
  }
  
  /**
   * A utility function to aid in properly allocating a call back function
   * passed into a native function.
   * @param callback
   * @return 
   */
  public BaseNode buildAndAllocateCallBack(BaseNode callback) {
    
    String funcName = callback.symbol().getSymbolAsString();
    int line = callback.getLine();
    BaseNode callBackFunc = new RowdyNode(callback.symbol(), line);
    
    funcName += ThreadLocalRandom.current().nextInt();
    
    Terminal idTerm = new Terminal("id", ID, funcName);
    AtomicId atomicId = new AtomicId(new NonTerminal("atomic-id", ATOMIC_ID, new int[][]{}), line);
    RowdyNode refAccess = new RowdyNode(new NonTerminal("ref-access", REF_ACCESS, new int[][]{}), line);
    RowdyNode id_ = new RowdyNode(new NonTerminal("id-", ID_, new int[][]{}), line);
    
    id_.add(new RowdyNode(idTerm, line));
    refAccess.add(id_);
    atomicId.add(refAccess);
    callBackFunc.add(atomicId);
    try {
      allocate(funcName, new Value(callback, false), callback.getLine());
    } catch (ConstantReassignmentException ex) {
      throw new RuntimeException(ex);
    }
    return callBackFunc;
  }
  
  public void handleException(Throwable e, boolean verbose) {
    System.err.println(e.getClass().getCanonicalName() + ": " + e.getLocalizedMessage());
    dumpCallStack();
    if (verbose) {
      e.printStackTrace();
    }
  }

  /**
   * Pushes a dummy function onto the call stack called 'shell'
   */
  public void runAsShell() {
    callStack.push(new Function("shell", new HashMap<>(), 0));
  }
}
