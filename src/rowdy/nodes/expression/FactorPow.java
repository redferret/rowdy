
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseRowdyNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.FACTOR;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_TAIL;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class FactorPow extends BaseRowdyNode {

  public FactorPow(Symbol symbol, int lineNumber) {
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
      return factorTail.execute(new Value(Math.pow(left, right), false));
    } else {
      return new Value(Math.pow(left, right), false);
    }
  }
  
}
