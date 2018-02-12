package rowdy;

import growdy.Terminal;
import rowdy.exceptions.ConstantReassignmentException;
import java.util.HashMap;
import java.util.Objects;

/**
 * A function in Rowdy always returns a value, has it's own symbol table, id
 * and the line it was called on. Functions are also used to initialize 
 * RowdyObjects.
 * @author Richard DeSilvey
 */
public class Function {

  private Value funcReturnValue;
  private final String name;
  private HashMap<String, Value> symbolTable;
  private final int lineCalledOn;
  private NativeJava nativeJavaHookin;
  
  public Function(String name, HashMap<String, Value> params, int lineCalledOn) {
    this.name = name;
    this.symbolTable = params;
    funcReturnValue = null;
    this.lineCalledOn = lineCalledOn;
  }

  public void setNativeJavaHookin(NativeJava nativeJavaHookin) {
    this.nativeJavaHookin = nativeJavaHookin;
  }
  
  public boolean isNativeFunction() {
    return nativeJavaHookin != null;
  }
  
  public int getLineCalledOn() {
    return lineCalledOn;
  }

  public void setReturnValue(Value value) {
    funcReturnValue = value;
  }

  public void allocate(String idName, Value value) throws ConstantReassignmentException {
    if (idName.equals("true") || idName.equals("false")) {
      return;
    }
    Value curValue;
    if (value == null) {
      value = new Value(null);
    }
    curValue = symbolTable.get(idName);
    if (curValue == null) {
      symbolTable.put(idName, value);
    } else {
      if (!curValue.isConstant()){
        symbolTable.replace(idName, value);
      } else {
        throw new ConstantReassignmentException(idName, this.lineCalledOn);
      }
    }
  }

  public void allocate(Terminal cur, Value value) throws ConstantReassignmentException {
    Function.this.allocate(cur.getName(), value);
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
  
  public Value getValue(String idName) {
    Value val = symbolTable.get(idName);
    return val;
  }
  
  public void unset(String idName) {
    symbolTable.remove(idName);
  }

  public String getName() {
    return name;
  }

  public Value getReturnValue() {
    return funcReturnValue;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Function){
      Function other = (Function)obj;
      if (other.name.equals(this.name)){
        return true;
      } else if (other == this){
        return true;
      }
    } 
    
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 89 * hash + Objects.hashCode(this.name);
    return hash;
  }
  
  @Override
  public String toString() {
    return name;
  }

  /**
   * @return A shallow copy of this function's symbol table
   */
  public HashMap getSymbolTable() {
    return (HashMap) this.symbolTable.clone();
  }
}
