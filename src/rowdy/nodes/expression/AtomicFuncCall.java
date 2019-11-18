
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.FUNC_CALL;

/**
 *
 * @author Richard
 */
public class AtomicFuncCall extends BaseNode {

  public AtomicFuncCall(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }

  @Override
  public Object execute(Object leftValue) {
    try {
      return instance.executeFunc(get(FUNC_CALL));
    } catch (ConstantReassignmentException ex) {
      throw new RuntimeException(ex);
    }
  }

}
