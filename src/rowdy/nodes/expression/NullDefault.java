
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC_ID;
import static rowdy.RowdyInstance.ATOMIC_GET;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;

/**
 *
 * @author Richard
 */
public class NullDefault extends BaseNode {

  public NullDefault(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }

  @Override
  public Object execute(Object leftValue) {
    int line = this.getLine();
    BaseNode idTestNode = get(ATOMIC_ID);
    BaseNode defaultValue = null;
    if (getAll().size() > 1) {
      defaultValue = this.getAll().get(1);
    }
    Value returnValue;
    Value testValue = instance.RAMAccess(idTestNode, new Value(), ATOMIC_GET, false);
    if (defaultValue == null) {
      if (testValue != null && testValue.getValue() != null) {
        return new Value(false);
      } else {
        return new Value(true);
      }
    } else {
      returnValue = new Value(((Value) defaultValue.execute()).getValue());
      if (testValue != null && testValue.getValue() != null) {
        return idTestNode.execute();
      }
      return returnValue;
    }
  }
  
}
