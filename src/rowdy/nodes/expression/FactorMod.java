
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
    return Calculator.calculate(leftValue, leftNode, tailNode, Calculator.Operation.MODULUS);
  }
  
}
