
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseRowdyNode;
import rowdy.Value;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class TermPlus extends BaseRowdyNode {

  public TermPlus(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }
  
  @Override
  public Value execute(Value leftValue) {
    BaseRowdyNode factor = getLeftMost();
    if (factor == null) {
      return instance.fetch(leftValue, this);
    }
    BaseRowdyNode factorTail = null;
    if (children.size() > 2) {
      factorTail = children.get(2);
    }
    leftValue = instance.fetch(leftValue, factor);
    double left = leftValue.valueToDouble();
    double right = factor.execute(leftValue).valueToDouble();
    if (factorTail != null) {
      return factorTail.execute(new Value(left + right, false));
    } else {
      return new Value(left + right, false);
    }
  }

}
