
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.RowdyRunner;
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
    Value valueToRound = runner.getValue(get(ID));
    double roundedValue = valueToRound.valueToDouble();
    int precision = runner.getValue(get(ARITHM_EXPR)).valueToDouble().intValue();
    double factor = 1;
    while (precision > 0) {
      factor *= 10;
      precision--;
    }
    roundedValue = (double) Math.round(roundedValue * factor) / factor;
    return new Value(roundedValue);
  }
  
}
