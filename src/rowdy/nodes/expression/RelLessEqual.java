
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
  public Value execute(Value leftValue) {
    BaseNode leftNode = getLeftMost();
    if (leftNode == null) {
      return instance.fetch(leftValue, this);
    }
    leftValue = instance.fetch(leftValue, this);
    return Calculator.calculate(leftValue, leftNode, null, Calculator.Operation.LESSEQUAL);
  }
}
