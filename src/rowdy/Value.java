package rowdy;

/**
 *
 * @author Richard DeSilvey
 */
public class Value {

  private Object value;
  private boolean isConstant;

  public Value() {
    value = null;
    isConstant = false;
  }

  public Value(Object value) {
    this.value = value;
    isConstant = false;
  }

  public void setAsConstant(boolean isConstant){
    this.isConstant = isConstant;
  }

  public boolean isConstant() {
    return isConstant;
  }
  
  public void setValue(Object value) {
    this.value = value;
  }

  public String valueToString() {
    if (value == null) {
      return null;
    }
    return value.toString().replaceAll("\"", "");
  }

  public Double valueToDouble() {
    if (value == null) {
      return null;
    }
    if (value instanceof String) {
      return Double.parseDouble((String) value);
    } else {
      return Double.parseDouble(value.toString());
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
      return null;
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

  public String toString() {
    if (value == null) {
      return "NULL";
    }
    return value.toString();
  }
}
