
package rowdy.nodes.expression;

import growdy.Symbol;
import growdy.Terminal;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import static rowdy.lang.RowdyGrammarConstants.ARITHM_EXPR;
import static rowdy.lang.RowdyGrammarConstants.ID;

/**
 *
 * @author Richard
 */
public class RoundExpr extends RowdyNode {

  public RoundExpr(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }

  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    Value valueToRound = runner.fetch(runner.getIdAsValue(get(ID)), this);
    double roundedValue = valueToRound.valueToDouble();
    ArithmExpr arithmExpr = (ArithmExpr) get(ARITHM_EXPR);
    int precision = arithmExpr.execute().valueToDouble().intValue();
    double factor = 1;
    while (precision > 0) {
      factor *= 10;
      precision--;
    }
    roundedValue = (double) Math.round(roundedValue * factor) / factor;
    return new Value(roundedValue);
  }
  
}
