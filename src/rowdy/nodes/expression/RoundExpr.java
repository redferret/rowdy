
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Value;
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
    
    if (valueToRound.getValue() instanceof Integer) {
      return new Value(valueToRound.getValue(), false);
    }
    
    double roundedValue = (double) valueToRound.getValue();
    BaseNode arithmExpr = getAll().get(3);
    int precision = (int) arithmExpr.execute().getValue();
    double factor = 1;
    while (precision > 0) {
      factor *= 10;
      precision--;
    }
    roundedValue = (double) Math.round(roundedValue * factor) / factor;
    return new Value(roundedValue, false);
  }
  
}
