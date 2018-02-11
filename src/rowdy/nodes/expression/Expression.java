
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import static rowdy.lang.RowdyGrammarConstants.*;

/**
 *
 * @author Richard
 */
public class Expression extends RowdyNode {

  public Expression(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    RowdyNode node = (RowdyNode) getLeftMost();
    if (node == null) {
      return runner.fetch(leftValue, this);
    }
    if (node.symbol().id() == BOOL_EXPR) {
      BoolTerm boolTerm = (BoolTerm) node.getLeftMost();
      BoolTermTail boolTermTail = (BoolTermTail) node.get(BOOL_TERM_TAIL);
      leftValue = boolTerm.execute();
      return boolTermTail.execute(leftValue);
    } else {
      return runner.executeExpr(this, leftValue);
    }
  }
  
}
