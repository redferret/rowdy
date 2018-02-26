
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.ARITHM_EXPR;
import static rowdy.lang.RowdyGrammarConstants.ID;

/**
 *
 * @author Richard
 */
public class RoundExpr extends BaseNode {

  public RoundExpr(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }

  @Override
  public Value execute(Value leftValue) {
    Value valueToRound = instance.fetch(instance.getIdAsValue(get(ID)), this);
    double roundedValue = valueToRound.valueToDouble();
    BaseNode arithmExpr = getAll().get(3);
    int precision = arithmExpr.execute().valueToDouble().intValue();
    double factor = 1;
    while (precision > 0) {
      factor *= 10;
      precision--;
    }
    roundedValue = (double) Math.round(roundedValue * factor) / factor;
    return new Value(roundedValue, false);
  }
  
}
