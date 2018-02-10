
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.FACTOR;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class FactorMinus extends RowdyNode {

  public FactorMinus(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    Factor factor = (Factor) get(FACTOR);
    leftValue = runner.fetch(leftValue, this);
    double leftVal = leftValue.valueToDouble();
    double rightVal = factor.execute(leftValue).valueToDouble();
    return factor.execute(new Value(leftVal - rightVal));
  }
}
