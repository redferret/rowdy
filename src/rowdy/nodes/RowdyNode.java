
package rowdy.nodes;

import growdy.Node;
import growdy.NonTerminal;
import growdy.Symbol;
import rowdy.BaseRowdyNode;
import rowdy.RowdyInstance;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;


/**
 *
 * @author Richard
 */
public class RowdyNode extends BaseRowdyNode {
  
  public RowdyNode(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  
  public static void initRunner(RowdyInstance runner) {
    RowdyNode.instance = runner;
  }
  
  /**
   * Override this method to implement your logic
   * @param leftValue
   * @return
   */
  @Override
  public Value execute(Value leftValue) {
    for (BaseRowdyNode node : children) {
      if (node.symbol() instanceof NonTerminal) {
        if (node instanceof RowdyNode) {
          leftValue = ((RowdyNode) node).execute(leftValue);
        } else {
          leftValue = node.execute(leftValue);
        }
      }
    }
    return leftValue;
  }
}
