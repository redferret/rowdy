
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Calculator;
import rowdy.Value;

/**
 *
 * @author Richard
 */
public class FactorMod extends BaseNode {
  
  public FactorMod(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }

  @Override
  public Object execute(Object leftValue) {
    BaseNode leftNode = getLeftMost();
    if (leftNode == null) {
      return instance.fetch((Value) leftValue, this);
    }
    BaseNode tailNode = null;
    if (children.size() > 1) {
      tailNode = children.get(1);
    }
    leftValue = instance.fetch((Value) leftValue, leftNode);
    Value rightValue = (Value) leftNode.execute(leftValue);
    return Calculator.calculate((Value) leftValue, rightValue, tailNode, Calculator.Operation.MODULUS);
  }
  
}
