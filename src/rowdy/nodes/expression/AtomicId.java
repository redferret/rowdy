
package rowdy.nodes.expression;

import growdy.Node;
import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.ID;
import static rowdy.lang.RowdyGrammarConstants.THIS_REF;

/**
 *
 * @author Richard
 */
public class AtomicId extends BaseNode {

  public AtomicId(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }

  @Override
  public Value execute(Value leftValue) {
    BaseNode child = get(ID);
    Value searchValue = new Value(child.symbol(), false), value;
    Node thisRef = get(THIS_REF, false);
    if (thisRef != null && thisRef.hasSymbols()) {
      value = instance.callStack.peek().getSymbolTable().getValue(searchValue);
      if (value == null) {
        throw new RuntimeException("The ID '" + searchValue + "' doesn't exist "
                + "on line " + getLine());
      }
    } else {
      value = instance.fetch(searchValue, this);
    }
    
    return value;
  }

}
