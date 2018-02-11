
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.BOOL_FACTOR_TAIL;
import rowdy.nodes.RowdyNode;

/**
 *fr = func(c) {f = (c * 1.8) + 32 return round f, 2}
 * @author Richard
 */
public class BoolTerm extends RowdyNode {
  
  public BoolTerm(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }

  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    BoolFactor boolFactor = (BoolFactor) getLeftMost();
    BoolFactorTail boolFactorTail = (BoolFactorTail) get(BOOL_FACTOR_TAIL);
    leftValue = boolFactor.execute(leftValue);
    return boolFactorTail.execute(leftValue);
  }
  
}
