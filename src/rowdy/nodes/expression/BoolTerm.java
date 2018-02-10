
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.BOOL_FACTOR_TAIL;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class BoolTerm extends RowdyNode {
  
  public BoolTerm(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }

  @Override
  public Value execute(RowdyNode cur, Value leftValue) throws ConstantReassignmentException {
    BoolFactor boolFactor = (BoolFactor) cur.getLeftMost();
    BoolFactorTail boolFactorTail = (BoolFactorTail) cur.get(BOOL_FACTOR_TAIL);
    leftValue = boolFactor.execute(leftValue);
    return boolFactorTail.execute(leftValue);
  }
  
}
