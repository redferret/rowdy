package rowdy;

/**
 *
 * @author Richard DeSilvey
 */
public class Value {

  private Object value;

  public Value() {
    value = null;
  }

  public Value(Object value) {
    this.value = value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public String valueToString() {
    if (value == null) {
      return null;
    }
    return value.toString();
  }

  public Double valueToNumber() {
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

  public Object getObject() {
    return value;
  }

  public String toString() {
    if (value == null) {
      return "NULL";
    }
    return value.toString();
  }
}
