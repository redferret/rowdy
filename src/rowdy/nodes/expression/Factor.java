
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class Factor extends RowdyNode {

  public Factor(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }
  @Override
  public Value execute(RowdyNode cur, Value leftValue) throws ConstantReassignmentException {
    RowdyNode child = (RowdyNode) cur.getLeftMost();
    return child.execute(leftValue);
  }
}
