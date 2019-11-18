package rowdy;

import growdy.Symbol;
import java.util.Objects;

/**
 * The wrapper for a value in Rowdy. This value can be anything, even a 
 * Node, or a Function, or even another Rowdy program.
 * @author Richard DeSilvey
 */
public class Value {

  public static enum Type {Integer, Double, Boolean, Short, Byte, Long, String, NaN}
  
  private Object value;
  private Type dataType;
  private boolean isConstant;
  private boolean isPublic;

  public Value() {
    this(null, false, true);
  }
  
  public Value(Object value) {
    this(value, false, true);
  }

  public Value(Object value, boolean isConstant) {
    this(value, isConstant, true);
  }
  public Value(Object value, boolean isConstant, boolean isPublic) {
    this.value = value;
    this.isConstant = isConstant;
    this.isPublic = isPublic;
    castValue();
  }

  public void isPublic(boolean isPublic) {
    this.isPublic = isPublic;
  }
  
  public boolean isPrivate() {
    return !isPublic;
  }
  
  public boolean isPublic() {
    return isPublic;
  }
  
  private void castValue() {
    if (value != null && value instanceof String) {
      String toCast = value.toString();
      if (isInteger(toCast)) {
        value = Integer.parseInt(toCast);
        dataType = Type.Integer;
      } else if (isLong(toCast)) {
        value = Long.parseLong(toCast);
        dataType = Type.Long;
      } else if (isDouble(toCast)) {
        value = Double.parseDouble(toCast);
        dataType = Type.Double;
      } else if (toCast.equals("false") || toCast.equals("true")) {
        value = Boolean.parseBoolean(toCast);
        dataType = Type.Boolean;
      } else {
        dataType = Type.String;
      }
    }
  }
  
  public void setAsConstant(boolean isConstant){
    this.isConstant = isConstant;
  }

  public boolean isConstant() {
    return isConstant;
  }
  
  public void setValue(Object value) {
    if (!isConstant) {
      this.value = value;
    }
  }

  
  public static boolean isInteger(String str) {
    try {
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public static boolean isLong(String str) {
    try {
      Long.parseLong(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
  
  public static boolean isDouble(String str) {
    try {
      Double.parseDouble(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
  
  public Symbol valueToSymbol() {
    if (value == null) {
      return null;
    }
    if (value instanceof Symbol) {
      return (Symbol) value;
    }
    return null;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Value) {
      Value vother = (Value) obj;
      return vother.value.equals(value);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 59 * hash + Objects.hashCode(this.value);
    hash = 59 * hash + (this.isConstant ? 1 : 0);
    return hash;
  }
  
  @Override
  public String toString() {
    if (value == null) {
      return "null";
    }
    return value.toString();
  }
}
