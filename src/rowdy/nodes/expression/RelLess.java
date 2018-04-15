
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Calculator;
import rowdy.Value;

/**
 *
 * @author Richard
 */
public class RelLess extends BaseNode  {

  public RelLess(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) {
    BaseNode leftNode = getLeftMost();
    if (leftNode == null) {
      return instance.fetch(leftValue, this);
    }
    leftValue = instance.fetch(leftValue, this);
    Value rightValue = leftNode.execute(leftValue);
    return Calculator.calculate(leftValue, rightValue, null, Calculator.Operation.LESS);
  }
}
