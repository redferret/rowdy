
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseRowdyNode;
import rowdy.Value;

/**
 *
 * @author Richard
 */
public class RelEqual extends BaseRowdyNode  {

  public RelEqual(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) {
    BaseRowdyNode arithmExpr = getLeftMost();
    if (arithmExpr == null) {
      return instance.fetch(leftValue, this);
    }
    leftValue = instance.fetch(leftValue, this);
    Value rightValue = arithmExpr.execute(leftValue);
    double left, right;
    if (leftValue.getValue() instanceof Boolean) {
      left = leftValue.valueToBoolean() ? 1 : 0;
    } else {
      left = leftValue.valueToDouble();
    }
    if (rightValue.getValue() instanceof Boolean) {
      right = rightValue.valueToBoolean() ? 1 : 0;
    } else {
      right = rightValue.valueToDouble();
    }
    return new Value(left == right, false);
  }
}
