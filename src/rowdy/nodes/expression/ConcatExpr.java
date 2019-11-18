
package rowdy.nodes.expression;

import growdy.Symbol;
import java.util.ArrayList;
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
  public Object execute(Object leftValue) {
    StringBuilder concatValue = new StringBuilder();
    
    BaseNode paramsNode = get(PARAMETERS);
    Value listValue = (Value) paramsNode.execute(new Value(new ArrayList<>(), false));
    List<BaseNode> params = (List<BaseNode>) listValue.getValue();
    
    params.forEach((expression) -> {
      concatValue.append(((Value)expression.execute()).getValue().toString());
    });
    return new Value(concatValue.toString(), false);
  }
  
}
