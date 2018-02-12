
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import static rowdy.lang.RowdyGrammarConstants.TERM_TAIL;

/**
 *
 * @author Richard
 */
public class ArithmExpr extends RowdyNode {

  public ArithmExpr(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    Term term = (Term) getLeftMost();
    TermTail termTail = (TermTail) get(TERM_TAIL);
    leftValue = term.execute(leftValue);
    return termTail.execute(leftValue);
  }
}
