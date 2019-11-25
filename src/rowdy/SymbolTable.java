
package rowdy;

import growdy.Terminal;
import java.util.HashMap;
import rowdy.exceptions.ConstantReassignmentException;

/**
 *
 * @author Richard
 */
public class SymbolTable {
  
  private final HashMap<String, Value> symbolTable;
  private final Object instance;
  
  public SymbolTable(HashMap<String, Value> symbolTable, Object instance) {
    this.symbolTable = symbolTable;
    this.instance = instance;
  }
  
  public Object getInstanceObject() {
    return instance;
  }
  
  public void allocate(String idName, Value value, int line, boolean forceAllocation) throws ConstantReassignmentException {
    if (idName.equals("true") || idName.equals("false")) {
      return;
    }
    Value curValue;
    if (value == null) {
      value = new Value();
    }
    curValue = symbolTable.get(idName);
    if (curValue == null) {
      if (!forceAllocation)
        checkScope(idName);
      symbolTable.put(idName, value);
    } else {
      if (!curValue.isConstant()){
        checkVisibility(curValue, idName);
        value.isPublic(curValue.isPublic());
        symbolTable.replace(idName, value);
      } else {
        throw new ConstantReassignmentException(idName, line);
      }
    }
  }

  public void allocate(Terminal cur, Value value, int line) throws ConstantReassignmentException {
    this.allocate(cur.getValue(), value, line, false);
  }
  
  public void allocate(HashMap<String, Value> table) {
    this.symbolTable.putAll(table);
  }

  /**
   * Frees up the memory used by this function. Deletes all the parameters and
   * local variables stored for this function and then nullifies the function's
   * symbol table.
   */
  public void free() {
    symbolTable.clear();
  }

  public Value getValue(String idName) {
    Value val = symbolTable.get(idName);
    if (val != null) {
      checkVisibility(val, idName);
    }
    return val;
  }
  
  private void checkScope(String idName) {
    if (instance instanceof RowdyObject) {
      
      if (BaseNode.instance.callStack.isEmpty()) {
        throw new RuntimeException("Can't assign new variable '" + idName + "' to existing instance");
      }
      
      Function topLevelFunc = BaseNode.instance.callStack.peek();

      if (topLevelFunc == null) {
        throw new RuntimeException("Can't assign new variable '" + idName + "' to existing instance");
      }

      RowdyObject classObject = topLevelFunc.getClassObject();
      if (classObject == null) {
        throw new RuntimeException("Can't assign new variable '" + idName + "' to existing instance");
      }

      boolean namesAreNotEqual = !classObject.getNameOfObject().equals(((RowdyObject)instance).getNameOfObject());
      if (namesAreNotEqual) {
        throw new RuntimeException("Can't assign new variable '" + idName + "' to existing instance");
      }
    }
  }
  
  private void checkVisibility(Value val, String idName) {
    if (instance instanceof RowdyObject && val.isPrivate()) {
      Function topLevelFunc = BaseNode.instance.callStack.peek();

      if (topLevelFunc == null) {
        throw new RuntimeException("Can't access private member '" + idName + "'");
      }

      RowdyObject classObject = topLevelFunc.getClassObject();
      if (classObject == null) {
        throw new RuntimeException("Can't access private member '" + idName + "'");
      }

      boolean namesAreNotEqual = !classObject.getNameOfObject().equals(((RowdyObject)instance).getNameOfObject());
      if (namesAreNotEqual) {
        throw new RuntimeException("Can't access private member '" + idName + "'");
      }
    }
  }
  
  public void unset(String idName) {
    symbolTable.remove(idName);
  }

  /**
   * Fetches the value stored or returns 'value' if no ID is stored in it.
   *
   * @param value The Value object that holds an atomic symbol. May contain an
   * ID or it may be a Constant.
   * @return A value object with an atomic symbol stored in it.
   */
  public Value getValue(Value value) {
    if (value == null) {
      return null;
    }
    if (value.getValue() instanceof Terminal) {
      String idName = ((Terminal) value.getValue()).getValue();
      return getValue(idName);
    } else {
      return value;
    }
  }
  
  @Override
  public String toString() {
    return symbolTable.toString();
  }
}
