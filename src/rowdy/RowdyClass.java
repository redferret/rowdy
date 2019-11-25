
package rowdy;

import java.util.List;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import static rowdy.lang.RowdyGrammarConstants.ASSIGN_STMT;
import static rowdy.lang.RowdyGrammarConstants.ASSIGN_VALUE;
import static rowdy.lang.RowdyGrammarConstants.CLASS_BODY;
import static rowdy.lang.RowdyGrammarConstants.CLASS_DEFS;
import static rowdy.lang.RowdyGrammarConstants.CONSTRUCTOR_METHOD;
import static rowdy.lang.RowdyGrammarConstants.CONST_OPT;
import static rowdy.lang.RowdyGrammarConstants.FUNCTION;
import static rowdy.lang.RowdyGrammarConstants.ID;
import static rowdy.lang.RowdyGrammarConstants.ID_;
import static rowdy.lang.RowdyGrammarConstants.ID_MODIFIER;
import static rowdy.lang.RowdyGrammarConstants.REF_ACCESS;
import static rowdy.lang.RowdyGrammarConstants.INHERIT_OPT;
import static rowdy.lang.RowdyGrammarConstants.PRIVATE_MEMBERS;
import static rowdy.lang.RowdyGrammarConstants.PUBLIC_MEMBERS;


/**
 *
 * @author Richard
 */
public class RowdyClass {
  private final String objectName;
  private final BaseNode constructor;
  private final BaseNode accessType;
  private final BaseNode inheritOpt;
  private final BaseNode publicMembers;
  private final BaseNode privateMembers;
  
  public RowdyClass(BaseNode classNode) {
    BaseNode classBody = classNode.get(CLASS_BODY);
    accessType = classNode.getLeftMost();
    objectName = classNode.get(ID).symbol().toString();
    inheritOpt = classNode.get(INHERIT_OPT);
    constructor = classBody.get(CONSTRUCTOR_METHOD);
    
    publicMembers = classBody.get(PUBLIC_MEMBERS);
    privateMembers = classBody.get(PRIVATE_MEMBERS);
    
  }

  public BaseNode getAccessType() {
    return accessType;
  }

  public String getObjectName() {
    return objectName;
  }
  
  public RowdyObject getInstance(String nameOfObject, List<Value> constructorParams){
    RowdyObject newInstance = new RowdyObject(nameOfObject);
    SymbolTable instanceTable = newInstance.getSymbolTable();
    
    if (publicMembers != null) {
      allocateMembers(publicMembers.get(CLASS_DEFS), instanceTable, true);
    }
    
    if (privateMembers != null) {
      allocateMembers(privateMembers.get(CLASS_DEFS), instanceTable, false);
    }
    
    if (constructor != null) {
      try {
        BaseNode.instance.executeFunc(objectName+"-construct", constructor, constructorParams, newInstance);
      } catch (ConstantReassignmentException ex) {
        throw new RuntimeException(ex);
      }
    }
    
    return newInstance;
  }

  private void allocateMembers(BaseNode classDefs, SymbolTable instanceTable, boolean isPublic) throws RuntimeException {
   while(classDefs != null) {
      BaseNode cur = classDefs.getLeftMost();
      int currentID = cur.symbol().id();
      switch (currentID) {
        case ASSIGN_STMT:
          BaseNode idThis = cur.get(REF_ACCESS);
          String id = idThis.get(ID_).get(ID).symbol().toString();
          BaseNode assignValue = cur.get(ASSIGN_VALUE);
          RowdyNode becomesExpr;
          Value value;
          if (assignValue != null) {
             becomesExpr = (RowdyNode) assignValue.getLeftMost();
             value = (Value) becomesExpr.execute();
          } else {
            value = new Value();
          }
          
          RowdyNode idModifier = (RowdyNode) cur.get(ID_MODIFIER);
          if (idModifier != null && idModifier.hasSymbols()) {
            switch (idModifier.getLeftMost().symbol().id()) {
              case CONST_OPT:
                value.setAsConstant(true);
                break;
            }
          }
          
          value.isPublic(isPublic);
          try {
            instanceTable.allocate(id, value, cur.getLine(), true);
          } catch (ConstantReassignmentException ex) {
            throw new RuntimeException(ex);
          }
          break;
        case FUNCTION:
          String functionName = cur.get(ID).symbol().toString();
          try {
            instanceTable.allocate(functionName, new Value(cur, true, isPublic), cur.getLine(), true);
          } catch (ConstantReassignmentException ex) {
            throw new RuntimeException(ex);
          }
          break;
      }
      classDefs = classDefs.get(CLASS_DEFS);
    }
  }
}
