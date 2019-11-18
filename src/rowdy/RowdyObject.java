
package rowdy;

import java.util.ArrayList;
import java.util.HashMap;
import rowdy.exceptions.ConstantReassignmentException;


/**
 *
 * @author Richard
 */
public class RowdyObject {
  
  private final String nameOfObject;
  private final SymbolTable instanceTable;
  
  public RowdyObject(String name) {
    nameOfObject = name;
    this.instanceTable = new SymbolTable(new HashMap<>(), this);
  }

  public String getNameOfObject() {
    return nameOfObject;
  }
  
  public SymbolTable getSymbolTable() {
    return this.instanceTable;
  }
  
  @Override
  public String toString() {
    Value funcVal = instanceTable.getValue("rep");
    if (funcVal != null) {
      BaseNode repNode = (BaseNode) funcVal.getValue();
      try { 
        return BaseNode.instance.executeFunc("rep", repNode, new ArrayList<>(), this).getValue().toString();
      } catch (ConstantReassignmentException ex) {
        throw new RuntimeException("On line " + repNode.getLine(), ex);
      }
    }
    return super.toString() + " : " + nameOfObject;
  }
}
