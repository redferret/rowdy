
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.FACTOR;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_TAIL;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_TAIL_DIV;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_TAIL_MOD;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_TAIL_MUL;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_TAIL_POW;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class FactorTail extends RowdyNode {

  public FactorTail(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }
  
  @Override
  public Value execute(RowdyNode cur, Value leftValue) throws ConstantReassignmentException {
    RowdyNode child = (RowdyNode) cur.getLeftMost();
    if (child == null) {
      return leftValue;
    }
    Factor factor = (Factor) child.get(FACTOR);
    FactorTail factorTail = (FactorTail) child.get(FACTOR_TAIL);
    leftValue = runner.fetch(leftValue, factor);
    double left = leftValue.valueToDouble();
    double right = factor.execute(leftValue).valueToDouble();
    switch(child.symbol().id()) {
      case FACTOR_TAIL_MUL:
        return factorTail.execute(new Value(left * right));
      case FACTOR_TAIL_DIV:
        return factorTail.execute(new Value(left / right));
      case FACTOR_TAIL_MOD:
        return factorTail.execute(new Value(left % right));
      case FACTOR_TAIL_POW:
        return factorTail.execute(new Value(Math.pow(left, right)));
      default:
        return leftValue;
    }
    
  }
}
