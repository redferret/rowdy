
package rowdy.nodes.statement;

import growdy.Node;
import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Function;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;
import rowdy.nodes.expression.Expression;

/**
 *
 * @author Richard
 */
public class ReturnStatement extends BaseNode {
  
  public ReturnStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value seqControlWrapper) {
    BaseNode seqControl = (BaseNode) seqControlWrapper.getValue();
    Function functionReturning = instance.callStack.peek();
    seqControl.setSeqActive(false);
    BaseNode returnExpr = get(EXPRESSION);
    Value toSet = returnExpr.execute();
    functionReturning.setReturnValue(toSet);
    return null;
  }
}
