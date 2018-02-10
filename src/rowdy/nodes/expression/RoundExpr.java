
package rowdy.nodes.expression;

import growdy.Node;
import growdy.Symbol;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.ARITHM_EXPR;
import static rowdy.lang.RowdyGrammarConstants.ID;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class RoundExpr extends RowdyNode {

  public RoundExpr(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }

  @Override
  public Value execute(RowdyNode roundExpr, Value leftValue) throws ConstantReassignmentException {
    Value valueToRound = runner.getValue(roundExpr.get(ID));
    double roundedValue = valueToRound.valueToDouble();
    int precision = runner.getValue(roundExpr.get(ARITHM_EXPR)).valueToDouble().intValue();
    double factor = 1;
    while (precision > 0) {
      factor *= 10;
      precision--;
    }
    roundedValue = (double) Math.round(roundedValue * factor) / factor;
    return new Value(roundedValue);
  }
  
}
