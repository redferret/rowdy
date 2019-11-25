
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.RowdyInstance.ATOMIC_GET;

/**
 *
 * @author Richard
 */
public class AtomicId extends BaseNode {

  public AtomicId(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }

  @Override
  public Object execute(Object leftValue) {
    return instance.RAMAccess(this, new Value(leftValue, false), ATOMIC_GET);
  }
  
}
