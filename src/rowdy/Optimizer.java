package rowdy;

import growdy.Node;
import growdy.NonTerminal;
import growdy.Terminal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import rowdy.nodes.expression.AtomicId;
import static rowdy.BaseNode.SAFE;
import static rowdy.BaseNode.UNSAFE;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC_CONST;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC_ID;
import static rowdy.lang.RowdyGrammarConstants.CLASS_DEF;
import static rowdy.lang.RowdyGrammarConstants.CLASS_DEFS;
import static rowdy.lang.RowdyGrammarConstants.CONCAT_EXPR;
import static rowdy.lang.RowdyGrammarConstants.CONSTANT;
import static rowdy.lang.RowdyGrammarConstants.DEFINITION;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;
import static rowdy.lang.RowdyGrammarConstants.EXPR_LIST;
import static rowdy.lang.RowdyGrammarConstants.FUNCTION;
import static rowdy.lang.RowdyGrammarConstants.FUNCTION_BODY;
import static rowdy.lang.RowdyGrammarConstants.FUNC_CALL;
import static rowdy.lang.RowdyGrammarConstants.FUNC_PARAMS;
import static rowdy.lang.RowdyGrammarConstants.ID;
import static rowdy.lang.RowdyGrammarConstants.NEW_OBJ;
import static rowdy.lang.RowdyGrammarConstants.PARAMETERS;
import static rowdy.lang.RowdyGrammarConstants.PARAMS_TAIL;
import static rowdy.lang.RowdyGrammarConstants.PRINT_STMT;
import static rowdy.lang.RowdyGrammarConstants.PRIVATE_SCOPE;
import static rowdy.lang.RowdyGrammarConstants.STMT_LIST;
import static rowdy.lang.RowdyGrammarConstants.THIS_;

/**
 *
 * @author Richard
 */
public class Optimizer {
  
  private final RowdyInstance instance;
  
  public Optimizer(RowdyInstance instance) {
    this.instance = instance;
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
            String idValueAsString = instance.getIdAsValue(id).toString();
            paramsList.add(idValueAsString);
            
            BaseNode paramsTailNode = funcParams.get(PARAMS_TAIL);
            while (paramsTailNode != null && paramsTailNode.hasSymbols()) {
              paramsList.add(instance.getIdAsValue(paramsTailNode.get(ID)).toString());
              paramsTailNode = paramsTailNode.get(PARAMS_TAIL);
            }
          }
          if (!paramsList.isEmpty()) {
            paramsId = "func-params " + curNode.getLine() + ThreadLocalRandom.current().nextInt();
            buildParameterNodeForParent(funcParams, paramsId, curNode.getLine());
            funcParams.setChildren(funcParams.get(PARAMETERS).getAll());
            instance.setAsGlobal(paramsId, new Value(paramsList, true));
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
            paramsId = instance.getIdAsValue(idNode).toString();
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
            instance.setAsGlobal(paramsId, new Value(params, true));
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
          instance.setAsGlobal(paramsId, new Value(baseValue, true));
      }
    }
  }
  
  public void listStmtsAndDefs(BaseNode root) {
    BaseNode workingRoot;
    List<BaseNode> children = root.getAll();
    
    for (int i = 0; i < children.size(); i++) {
      workingRoot = children.get(i);
      switch(workingRoot.symbol().id()) {
        case DEFINITION:
        case STMT_LIST:
        case CLASS_DEFS:
          scanRoot(workingRoot);
          scanChildren(workingRoot);
          break;
        default:
          listStmtsAndDefs(workingRoot);
      }
    }
  }
  
  private void scanChildren(BaseNode workingRoot) {
    List<BaseNode> workingChildren = workingRoot.getAll();
    workingChildren.forEach((child) -> {
      listStmtsAndDefs(child);
    });
  }

  private void scanRoot(BaseNode workingRoot) {
    BaseNode nextRoot;
    BaseNode leftChild;
    BaseNode replacementRoot;
    int workingRootSymbolId = workingRoot.symbol().id();
    List<BaseNode> workingChildren = workingRoot.getAll();
    while (true) {
      nextRoot = workingRoot.get(workingRootSymbolId);
      if (nextRoot != null) {
        leftChild = nextRoot.getLeftMost();
        replacementRoot = nextRoot.get(workingRootSymbolId);
        workingChildren.remove(workingChildren.size() - 1);
        workingRoot.add(leftChild);
        if (replacementRoot != null){
          workingChildren.add(replacementRoot);
        }
      } else {
        break;
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
    listStmtsAndDefs(root);
    checkForUnsafeFunctions(root);
  }
}
