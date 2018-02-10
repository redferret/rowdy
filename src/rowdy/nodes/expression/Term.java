
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_TAIL;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class Term extends RowdyNode {
  
  public Term(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }
  @Override
  public Value execute(RowdyNode cur, Value leftValue) throws ConstantReassignmentException {
    Factor factor = (Factor) cur.getLeftMost();
    FactorTail factorTail = (FactorTail) cur.get(FACTOR_TAIL);
    leftValue = factor.execute(leftValue);
    return factorTail.execute(leftValue);
  }
}
