
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.ID;

/**
 *
 * @author Richard
 */
public class IssetExpr extends BaseNode {

  public IssetExpr(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }

  @Override
  public Object execute(Object leftValue) {
    Value idTerm = instance.getIdAsValue(get(ID));
    Value resultBoolean = new Value(instance.isset(idTerm), false);
    return resultBoolean;
  }
}
