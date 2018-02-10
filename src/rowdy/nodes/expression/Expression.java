
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import static rowdy.lang.RowdyGrammarConstants.*;

/**
 *
 * @author Richard
 */
public class Expression extends RowdyNode {

  public Expression(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }
  @Override
  public Value execute(RowdyNode cur, Value leftValue) throws ConstantReassignmentException {
    RowdyNode node = (RowdyNode) cur.getLeftMost();
    BoolTerm boolTerm = (BoolTerm) node.getLeftMost();
    BoolTermTail boolTermTail = (BoolTermTail) node.get(BOOL_TERM_TAIL);
    leftValue = boolTerm.execute();
    return boolTermTail.execute(leftValue);
  }
  
}
