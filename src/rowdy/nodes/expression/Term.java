
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
  
  public Term(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    Factor factor = (Factor) getLeftMost();
    FactorTail factorTail = (FactorTail) get(FACTOR_TAIL);
    leftValue = factor.execute(leftValue);
    return factorTail.execute(leftValue);
  }
}
