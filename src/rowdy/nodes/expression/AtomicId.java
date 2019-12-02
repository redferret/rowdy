
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Calculator;
import static rowdy.RowdyInstance.ATOMIC_GET;
import static rowdy.RowdyInstance.ATOMIC_SET;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.DECREMENT_EXPR;
import static rowdy.lang.RowdyGrammarConstants.INCREMENT_EXPR;
import static rowdy.lang.RowdyGrammarConstants.POST_INC_DEC;

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
    
    BaseNode postIncDecNode = get(POST_INC_DEC);
    
    if (postIncDecNode != null && postIncDecNode.hasSymbols()) {
      Value assignValue = new Value();
      instance.RAMAccess(this, assignValue, ATOMIC_GET);
      switch (postIncDecNode.getLeftMost().symbol().id()) {
        case INCREMENT_EXPR:
          assignValue = Calculator.calculate(assignValue, new Value(1), null, Calculator.Operation.ADD);
          break;
        case DECREMENT_EXPR:
          assignValue = Calculator.calculate(assignValue, new Value(-1), null, Calculator.Operation.ADD);
          break;
      }
      instance.RAMAccess(this, assignValue, ATOMIC_SET);
      return assignValue;
    }
    return instance.RAMAccess(this, new Value(leftValue, false), ATOMIC_GET);
  }
  
}
