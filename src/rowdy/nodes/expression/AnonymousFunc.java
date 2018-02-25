
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseRowdyNode;
import rowdy.Value;

/**
 *
 * @author Richard
 */
public class AnonymousFunc extends BaseRowdyNode {

  public AnonymousFunc(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }

  @Override
  public Value execute(Value leftValue) {
    return new Value(this, false);
  }
  
}
