
package rowdy.nodes.expression;

import growdy.Symbol;
import growdy.Terminal;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.CONST;
import static rowdy.lang.RowdyGrammarConstants.FUNC_CALL;
import static rowdy.lang.RowdyGrammarConstants.ID;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class Atomic extends RowdyNode {

  public Atomic(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }
  @Override
  public Value execute(RowdyNode cur, Value leftValue) throws ConstantReassignmentException {
    RowdyNode child = (RowdyNode) cur.getLeftMost().getLeftMost();
    switch(child.symbol().id()) {
      case ID:
        return new Value(child.symbol());
      case CONST:
        return new Value(((Terminal) child.symbol()).getName());
      case FUNC_CALL:
        return runner.executeFunc(child);
      default:
        return leftValue;
    }
  }
}
