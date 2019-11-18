
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Calculator;
import rowdy.Value;

/**
 *
 * @author Richard
 */
public class RelLessEqual extends BaseNode  {

  public RelLessEqual(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }
  @Override
  public Object execute(Object leftValue) {
    BaseNode leftNode = getLeftMost();
    if (leftNode == null) {
      return instance.fetch((Value) leftValue, this);
    }
    leftValue = instance.fetch((Value) leftValue, this);
    Value rightValue = (Value) leftNode.execute(leftValue);
    return Calculator.calculate((Value) leftValue, rightValue, null, Calculator.Operation.LESSEQUAL);
  }
}
