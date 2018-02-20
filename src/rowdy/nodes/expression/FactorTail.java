
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.Value;
import rowdy.nodes.RowdyNode;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.FACTOR;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_TAIL;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_TAIL_DIV;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_TAIL_MOD;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_TAIL_MUL;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_TAIL_POW;

/**
 *
 * @author Richard
 */
public class FactorTail extends RowdyNode {

  public FactorTail(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    RowdyNode child = (RowdyNode) getLeftMost();
    if (child == null) {
      return instance.fetch(leftValue, this);
    }
    Factor factor = (Factor) child.get(FACTOR);
    FactorTail factorTail = (FactorTail) child.get(FACTOR_TAIL);
    leftValue = instance.fetch(leftValue, factor);
    double left = leftValue.valueToDouble();
    double right = factor.execute(leftValue).valueToDouble();
    switch(child.symbol().id()) {
      case FACTOR_TAIL_MUL:
        return factorTail.execute(new Value(left * right, false));
      case FACTOR_TAIL_DIV:
        return factorTail.execute(new Value(left / right, false));
      case FACTOR_TAIL_MOD:
        return factorTail.execute(new Value(left % right, false));
      case FACTOR_TAIL_POW:
        return factorTail.execute(new Value(Math.pow(left, right), false));
      default:
        return leftValue;
    }
    
  }
}
