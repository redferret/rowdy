
package rowdy.nodes.expression;

import growdy.Node;
import growdy.Symbol;
import java.util.ArrayList;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.BOOL_TERM;
import static rowdy.lang.RowdyGrammarConstants.BOOL_TERM_TAIL;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class BoolTermTail extends RowdyNode {
  
  public BoolTermTail(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    ArrayList<Node> boolChildren = getAll();
    if (boolChildren.isEmpty()) {
      return runner.fetch(leftValue, this);
    }
    leftValue = runner.fetch(leftValue, this);
    boolean bLeft = leftValue.valueToBoolean();
    BoolTerm boolTerm = (BoolTerm) get(BOOL_TERM);
    boolean bRight = boolTerm.execute(leftValue).valueToBoolean();
    BoolTermTail boolTermTail = (BoolTermTail) get(BOOL_TERM_TAIL);
    
    return boolTermTail.execute(new Value(bLeft || bRight));
  }
}
