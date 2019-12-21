
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
    Value returnValue;
    if (returnExpr != null) {
      returnValue = (Value) returnExpr.execute();
    } else {
      returnValue = new Value();
    }
    functionReturning.setReturnValue(returnValue);
    functionReturning.activeLoops.forEach(loop -> {
      loop.setSeqActive(false);
    });
    return null;
  }
}
