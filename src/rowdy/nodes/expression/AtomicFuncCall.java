
package rowdy.nodes.expression;

import growdy.Symbol;
import java.util.logging.Level;
import java.util.logging.Logger;
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
  public Value execute(Value leftValue) {
    try {
      return instance.executeFunc(get(FUNC_CALL));
    } catch (ConstantReassignmentException ex) {
      Logger.getLogger(AtomicId.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

}
