
package rowdy;

import growdy.Terminal;
import java.util.HashMap;
import rowdy.exceptions.ConstantReassignmentException;

/**
 *
 * @author Richard
 */
public class SymbolTable {
  
  private HashMap<String, Value> symbolTable;
  
  public SymbolTable(HashMap<String, Value> symbolTable) {
    this.symbolTable = symbolTable;
  }
  
  public void allocate(String idName, Value value, int line) throws ConstantReassignmentException {
    if (idName.equals("true") || idName.equals("false")) {
      return;
    }
    Value curValue;
    if (value == null) {
      value = new Value();
    }
    curValue = symbolTable.get(idName);
    if (curValue == null) {
      symbolTable.put(idName, value);
    } else {
      if (!curValue.isConstant()){
        symbolTable.replace(idName, value);
      } else {
        throw new ConstantReassignmentException(idName, line);
      }
    }
  }

  public void allocate(Terminal cur, Value value, int line) throws ConstantReassignmentException {
    this.allocate(cur.getName(), value, line);
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
    symbolTable = null;
  }

  public Value getValue(String idName) {
    Value val = symbolTable.get(idName);
    return val;
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
      String idName = ((Terminal) value.getValue()).getName();
      return getValue(idName);
    } else {
      return value;
    }
  }
}
