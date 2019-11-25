
package rowdy.nodes.statement;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Function;
import rowdy.Value;

/**
 *
 * @author Richard
 */
public class ReturnStatement extends BaseNode {
  
  public ReturnStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Object execute(Object seqControl) {
    Function functionReturning = instance.callStack.peek();
    ((BaseNode) seqControl).setSeqActive(false);
    BaseNode returnExpr = getLeftMost();
    Value toSet = (Value) returnExpr.execute();
    functionReturning.setReturnValue(toSet);
    return null;
  }
}
