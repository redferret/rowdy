
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import static rowdy.lang.RowdyGrammarConstants.TERM_TAIL;

/**
 *
 * @author Richard
 */
public class ArithmExpr extends RowdyNode {

  public ArithmExpr(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }
  @Override
  public Value execute(RowdyNode cur, Value leftValue) throws ConstantReassignmentException {
    Term term = (Term) cur.getLeftMost();
    TermTail termTail = (TermTail) cur.get(TERM_TAIL);
    leftValue = term.execute(leftValue);
    return termTail.execute(leftValue);
  }
}
