
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.FACTOR;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_TAIL;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class FactorPow extends BaseNode {

  public FactorPow(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }

  @Override
  public Value execute(Value leftValue) {
    BaseNode factor = getLeftMost();
    if (factor == null) {
      return instance.fetch(leftValue, this);
    }
    BaseNode factorTail = null;
    if (children.size() > 2) {
      factorTail = children.get(2);
    }
    leftValue = instance.fetch(leftValue, factor);
    double left = leftValue.valueToDouble();
    double right = factor.execute(leftValue).valueToDouble();
    if (factorTail != null) {
      return factorTail.execute(new Value(Math.pow(left, right), false));
    } else {
      return new Value(Math.pow(left, right), false);
    }
  }
  
}
