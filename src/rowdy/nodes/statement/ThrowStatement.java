
package rowdy.nodes.statement;

import growdy.Symbol;
import growdy.Terminal;
import rowdy.BaseNode;
import rowdy.exceptions.ThrownException;

/**
 *
 * @author Richard
 */
public class ThrowStatement extends BaseNode {

  public ThrowStatement(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }

  @Override
  public Object execute(Object leftValue) {
    BaseNode msg = this.getLeftMost(true);
    throw new ThrownException(((Terminal) msg.symbol()).getValue());
  }
  
}
