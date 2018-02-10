
package rowdy.nodes.expression;

import growdy.Node;
import growdy.Symbol;
import java.util.ArrayList;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.AND;
import static rowdy.lang.RowdyGrammarConstants.BOOL_TERM;
import static rowdy.lang.RowdyGrammarConstants.BOOL_TERM_TAIL;
import static rowdy.lang.RowdyGrammarConstants.OR;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class BoolTermTail extends RowdyNode {
  
  public BoolTermTail(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }
  
  @Override
  public Value execute(RowdyNode cur, Value leftValue) throws ConstantReassignmentException {
    ArrayList<Node> boolChildren = cur.getAll();
    if (boolChildren.isEmpty()) {
      return leftValue;
    }
    leftValue = runner.fetch(leftValue, cur);
    boolean bLeft = leftValue.valueToBoolean();
    BoolTerm boolTerm = (BoolTerm) cur.get(BOOL_TERM);
    boolean bRight = boolTerm.execute(leftValue).valueToBoolean();
    BoolTermTail boolTermTail = (BoolTermTail) cur.get(BOOL_TERM_TAIL);
    
    return boolTermTail.execute(new Value(bLeft || bRight));
  }
}
