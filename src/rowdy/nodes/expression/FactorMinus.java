
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Calculator;
import rowdy.Value;

/**
 *
 * @author Richard
 */
public class FactorMinus extends BaseNode {

  public FactorMinus(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) {
    BaseNode leftNode = getLeftMost();
    if (leftNode == null) {
      return instance.fetch(leftValue, this);
    }
    BaseNode tailNode = null;
    if (children.size() > 2) {
      tailNode = children.get(2);
    }
    leftValue = instance.fetch(leftValue, leftNode);
    Value rightValue = leftNode.execute(leftValue);
    return Calculator.calculate(leftValue, rightValue, tailNode, Calculator.Operation.SUBTRACT);
  }
}
