package rowdy;

import growdy.Symbol;
import growdy.Terminal;
import java.util.Objects;

/**
 * The wrapper for a value in Rowdy. This value can be anything, even a 
 * Node, or a Function, or even another Rowdy program.
 * @author Richard DeSilvey
 */
public class Value {

  private Object value;
  private boolean isConstant;

  public Value() {
    this(null, false);
  }

  public Value(Object value, boolean isConstant) {
    this.value = value;
    this.isConstant = isConstant;
  }
  
  public Value(Value v){
    this(v.getValue(), false);
  }
  
  public Value(Object value) {
    this(value, false);
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

  public String valueToString() {
    if (value == null) {
      return "null";
    }
    return value.toString().replaceAll("\"", "");
  }

  public Double valueToDouble() {
    if (value == null) {
      return 0d;
    }
    if (value instanceof String) {
      return Double.parseDouble((String) value);
    } else {
      Double v;
      try {
        v = Double.parseDouble(value.toString());
      } catch (Exception e){
        v = 0d;
      }
      return v;
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

  public Boolean valueToBoolean() {
    if (value == null) {
      return false;
    }
    if (value instanceof Terminal) {
      return Boolean.parseBoolean(((Terminal) value).getName());
    } else if (value instanceof Value) {
      return Boolean.parseBoolean(value.toString());
    } else if (value instanceof String) {
      return Boolean.parseBoolean(value.toString());
    }
    return (Boolean) value;
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
  
  public String toString() {
    if (value == null) {
      return "null";
    }
    return value.toString();
  }
}
