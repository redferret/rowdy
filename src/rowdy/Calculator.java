
package rowdy;

/**
 *
 * @author Richard
 */
public class Calculator {
  public static enum Operation {
    ADD, SUBTRACT, DIVIDE, MULTIPLY, MODULUS, POW, LESS, LESSEQUAL, GREATER, GREATEREQUAL
  }

  public static Value calculate(Value leftValue, BaseNode leftNode, BaseNode tailNode, Operation operation) {
    
    Object left = (leftValue == null) ? 0 : leftValue.getValue();
    Object right = leftNode.execute(leftValue).getValue();
    Value.Type castTo;

    if (left instanceof Double && right instanceof Double) {
      castTo = Value.Type.Double;
    } else if (left instanceof Double || right instanceof Double) {
      if (left instanceof Integer) {
        left = (Double) Double.sum(0, (Integer)left);
        castTo = Value.Type.Double;
      } else if (left instanceof Long) {
        right = Long.sum(0, ((Double)right).intValue());
        castTo = Value.Type.Long;
      } else if (right instanceof Integer) {
        right = (Double) Double.sum(0, (Integer)right);
        castTo = Value.Type.Double;
      } else if (right instanceof Long) {
        left = Long.sum(0, ((Double)left).intValue());
        castTo = Value.Type.Long;
      } else {
        castTo = Value.Type.String;
      }
    } else if (left instanceof Integer && right instanceof Integer) {
      castTo = Value.Type.Integer;
    } else if (left instanceof Long && right instanceof Long) {
      castTo = Value.Type.Long;
    } else if (left instanceof Long || right instanceof Long) {
      castTo = Value.Type.Long;
      if (left instanceof Integer) {
        left = Long.sum(0, (Integer) left);
      } else if (right instanceof Integer) {
        right = Long.sum(0, (Integer)right);
      } else if (left instanceof Double) {
        left = Long.sum(0, ((Double)left).intValue());
      } else if (right instanceof Double) {
        right = Long.sum(0, ((Double)right).intValue());
      } else {
        castTo = Value.Type.String;
      }
    } else if (left instanceof Boolean && right instanceof Boolean) {
      castTo = Value.Type.Boolean;
    } else if (left instanceof String || right instanceof String) {
      castTo = Value.Type.String;
    } else {
      return new Value(Double.NaN, false);
    }
    return calculate(left, right, tailNode, operation, castTo);
    
  }

  private static Value calculate(Object left, Object right, BaseNode tailNode, Operation operation, Value.Type type) {
    
    Value value = new Value(null, false);
    switch (operation) {
      case ADD:
        value = add(left, right, type);
        break;
      case SUBTRACT:
        value = subtract(left, right, type);
        break;
      case MULTIPLY:
        value = multiply(left, right, type);
        break;
      case DIVIDE:
        value = divide(left, right, type);
        break;
      case MODULUS:
        value = mod(left, right, type);
        break;
      case POW:
        value = power(left, right, type);
        break;
      case LESS:
        value = lessThan(left, right, type);
        break;
      case LESSEQUAL:
        value = lessOrEqual(left, right, type);
        break;
      case GREATER:
        value = greaterThan(left, right, type);
        break;
      case GREATEREQUAL:
        value = greaterOrEqual(left, right, type);
        break;
    }
    
    if (tailNode != null) {
      return tailNode.execute(value);
    } else {
      return value;
    }
  }

  private static Value lessThan(Object a, Object b, Value.Type type) {
    switch(type) {
      case Integer:
        return new Value((int)a < (int) b, false);
      case Long:
        return new Value((long)a < (long) b, false);
      case Double:
        return new Value((double)a < (double) b, false);
      default:
        return new Value(Double.NaN, false);
    }
  }
  
  private static Value lessOrEqual(Object a, Object b, Value.Type type) {
    switch(type) {
      case Integer:
        return new Value((int)a <= (int) b, false);
      case Long:
        return new Value((long)a <= (long) b, false);
      case Double:
        return new Value((double)a <= (double) b, false);
      default:
        return new Value(Double.NaN, false);
    }
  }
  
  private static Value greaterThan(Object a, Object b, Value.Type type) {
    switch(type) {
      case Integer:
        return new Value((int)a > (int) b, false);
      case Long:
        return new Value((long)a > (long) b, false);
      case Double:
        return new Value((double)a > (double) b, false);
      default:
        return new Value(Double.NaN, false);
    }
  }
  
  private static Value greaterOrEqual(Object a, Object b, Value.Type type) {
    switch(type) {
      case Integer:
        return new Value((int)a >= (int) b, false);
      case Long:
        return new Value((long)a >= (long) b, false);
      case Double:
        return new Value((double)a >= (double) b, false);
      default:
        return new Value(Double.NaN, false);
    }
  }
  
  private static Value add(Object a, Object b, Value.Type type) {
    switch(type) {
      case Integer:
        return new Value((int)a + (int) b, false);
      case Long:
        return new Value((long)a + (long) b, false);
      case Double:
        return new Value((double)a + (double) b, false);
      default:
        return new Value(Double.NaN, false);
    }
  }
  
  private static Value subtract(Object a, Object b, Value.Type type) {
    switch(type) {
      case Integer:
        return new Value((int)a - (int) b, false);
      case Long:
        return new Value((long)a - (long) b, false);
      case Double:
        return new Value((double)a - (double) b, false);
      default:
        return new Value(Double.NaN, false);
    }
  }
  
  private static Value multiply(Object a, Object b, Value.Type type) {
    switch(type) {
      case Integer:
        return new Value((int)a * (int) b, false);
      case Long:
        return new Value((long)a * (long) b, false);
      case Double:
        return new Value((double)a * (double) b, false);
      default:
        return new Value(Double.NaN, false);
    }
  }
  
  private static Value divide(Object a, Object b, Value.Type type) {
    switch(type) {
      case Integer:
        if ((int)b == 0) {
          return new Value(Double.NaN, false);
        }
        return new Value((int)a / (int) b, false);
      case Long:
        if ((long)b == 0) {
          return new Value(Double.NaN, false);
        }
        return new Value((long)a / (long) b, false);
      case Double:
        if ((double)b == 0) {
          return new Value(Double.NaN, false);
        }
        return new Value((double)a / (double) b, false);
      default:
        return new Value(Double.NaN, false);
    }
  }
  
  private static Value mod(Object a, Object b, Value.Type type) {
    switch(type) {
      case Integer:
        return new Value((int)a % (int) b, false);
      case Long:
        return new Value((long)a % (long) b, false);
      case Double:
        return new Value((double)a % (double) b, false);
      default:
        return new Value(Double.NaN, false);
    }
  }
  
  private static Value power(Object a, Object b, Value.Type type) {
    switch(type) {
      case Integer:
        return new Value((int)Math.pow((int)a, (int)b), false);
      case Long:
        return new Value((long)Math.pow((long)a, (long)b), false);
      case Double:
        return new Value((double)Math.pow((double)a, (double)b), false);
      default:
        return new Value(Double.NaN, false);
    }
  }
  
}
