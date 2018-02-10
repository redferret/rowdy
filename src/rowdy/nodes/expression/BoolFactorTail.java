
package rowdy.nodes.expression;

import growdy.Node;
import growdy.Symbol;
import java.util.ArrayList;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.BOOL_FACTOR;
import static rowdy.lang.RowdyGrammarConstants.BOOL_FACTOR_TAIL;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class BoolFactorTail extends RowdyNode {
  
  public BoolFactorTail(Symbol def, int lineNumber, RowdyRunner runner) {
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
    BoolFactor boolFactor = (BoolFactor) cur.get(BOOL_FACTOR);
    boolean bRight = boolFactor.execute(leftValue).valueToBoolean();
    BoolFactorTail boolFactorTail = (BoolFactorTail) cur.get(BOOL_FACTOR_TAIL);
    
    return boolFactorTail.execute(new Value(bLeft && bRight));
  }
}
