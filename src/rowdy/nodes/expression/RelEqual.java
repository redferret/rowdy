
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Value;

/**
 *
 * @author Richard
 */
public class RelEqual extends BaseNode  {

  public RelEqual(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) {
    BaseNode arithmExpr = getLeftMost();
    if (arithmExpr == null) {
      return instance.fetch(leftValue, this);
    }
    leftValue = instance.fetch(leftValue, this);
    Value rightValue = arithmExpr.execute(leftValue);
    Number left, right;
    if (leftValue.getValue() instanceof Boolean) {
      left = (boolean) leftValue.getValue() ? 1 : 0;
    } else if (leftValue.getValue() instanceof String) {
      return new Value(false, false);
    } else {
      left = (Number) leftValue.getValue();
    }
    if (rightValue.getValue() instanceof Boolean) {
      right = (boolean) rightValue.getValue() ? 1 : 0;
    } else if (rightValue.getValue() instanceof String) {
      return new Value(false, false);
    } else {
      right = (Number) rightValue.getValue();
    }
    
    if (left instanceof Double && right instanceof Integer) {
      right = (double) ((Integer)right + 0.0d);
    } else if (right instanceof Double && left instanceof Integer) {
      left = (double) ((Integer)left + 0.0d);
    }
    
    if (left instanceof Integer && right instanceof Long) {
      left = (long) ((Integer)left + 0L);
    } else if (right instanceof Integer && left instanceof Long) {
      right = (long) ((Integer)right + 0L);
    }
    
    return new Value(left.equals(right), false);
  }
}
