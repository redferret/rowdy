
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import static rowdy.RowdyInstance.ATOMIC_GET;
import rowdy.SymbolTable;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC_ID;
import static rowdy.lang.RowdyGrammarConstants.REF_ACCESS;

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
    BaseNode idTestNode = get(ATOMIC_ID);
    BaseNode defaultValue = get(ATOMIC_ID, 1);
    Value returnValue = new Value(((Value) defaultValue.execute()).getValue());
    Value testValue = instance.atomicAccess(idTestNode, new Value(), ATOMIC_GET, false);
    if (testValue != null && testValue.getValue() != null) {
      return idTestNode.execute();
    }
    return returnValue;
  }
  
  
}
