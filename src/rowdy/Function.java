package rowdy;

import java.util.HashMap;

public class Function {

  private Value funcReturnValue;
  private String name;
  private HashMap<String, Value> symbolTable;

  public Function(String name, HashMap<String, Value> params) {
    this.name = name;
    this.symbolTable = params;
    funcReturnValue = null;
  }

  public void setReturnValue(Value value) {
    funcReturnValue = value;
  }

  public void setIDValue(String idName, Value value) {
    Value curValue;
    if (value == null) {
      value = new Value("null");
    }
    curValue = symbolTable.get(idName);
    if (curValue == null) {
      symbolTable.put(idName, value);
    } else {
      symbolTable.remove(idName);
      symbolTable.put(idName, value);
    }
  }

  public void setIDValue(Terminal cur, Value value) {
    setIDValue(cur.getName(), value);
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

  /**
   * Fetches the value stored or returns 'value' if no ID is stored in it.
   *
   * @param value The Value object that holds an atomic symbol. May contain an
   * ID or it may be a Constant.
   * @param error throw any RuntimeExceptions or not
   * @return A value object with an atomic symbol stored in it.
   */
  public Value getValue(Value value, boolean error) {
    if (value == null) {
      return null;
    }
    if (((Value) value).getObject() instanceof Terminal) {
      String v = ((Terminal) value.getObject()).getName();
      Value val = symbolTable.get(v);
      if (val == null && error) {
        throw new RuntimeException("Unknown "
                + "identifier: " + v);
      } else if (val == null && !error) {
        return null;
      }
      return val;
    } else {
      return value;
    }
  }

  public String getName() {
    return name;
  }

  public Value getReturnValue() {
    return funcReturnValue;
  }

  @Override
  public String toString() {
    return name;
  }
}
