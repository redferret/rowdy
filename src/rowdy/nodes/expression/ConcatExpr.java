
package rowdy.nodes.expression;

import growdy.Symbol;
import java.util.List;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.PARAMETERS;

/**
 *
 * @author Richard
 */
public class ConcatExpr extends BaseNode {

  public ConcatExpr(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }

  @Override
  public Value execute(Value leftValue) {
    StringBuilder concatValue = new StringBuilder();
    
    BaseNode paramsNode = get(PARAMETERS);
    List<BaseNode> params = (List<BaseNode>) paramsNode.execute().getValue();
    
    params.forEach((expression) -> {
      concatValue.append(expression.execute().valueToString());
    });
    return new Value(concatValue.toString(), false);
  }
  
}
