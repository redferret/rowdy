
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Calculator;
import rowdy.Value;

/**
 *
 * @author Richard
 */
public class TermMinus extends BaseNode{

  public TermMinus(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }
  
  @Override
  public Value execute(Value leftValue) {
    BaseNode leftNode = getLeftMost();
    if (leftNode == null) {
      return instance.fetch(leftValue, this);
    }
    BaseNode tailNode = null;
    if (children.size() > 1) {
      tailNode = children.get(1);
    }
    leftValue = instance.fetch(leftValue, leftNode);
    
    return Calculator.calculate(leftValue, leftNode, tailNode, Calculator.Operation.SUBTRACT);
  }
  
}
