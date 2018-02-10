
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.TERM;
import static rowdy.lang.RowdyGrammarConstants.TERM_MINUS;
import static rowdy.lang.RowdyGrammarConstants.TERM_PLUS;
import static rowdy.lang.RowdyGrammarConstants.TERM_TAIL;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class TermTail extends RowdyNode {
  
  public TermTail(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }
  @Override
  public Value execute(RowdyNode cur, Value leftValue) throws ConstantReassignmentException {
    RowdyNode child = (RowdyNode) cur.getLeftMost();
    if (child == null) {
      return leftValue;
    }
    Term term = (Term) child.get(TERM);
    TermTail termTail = (TermTail) child.get(TERM_TAIL);
    leftValue = runner.fetch(leftValue, child);
    double left = leftValue.valueToDouble();
    double right = term.execute(leftValue).valueToDouble();
    switch(child.symbol().id()) {
      case TERM_PLUS:
        return termTail.execute(new Value(left + right));
      case TERM_MINUS:
        return termTail.execute(new Value(left - right));
      default:
        return leftValue;
    }
    
  }
}
