
package rowdy.nodes;

import growdy.NonTerminal;
import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.RowdyInstance;


/**
 *
 * @author Richard
 */
public class RowdyNode extends BaseNode {
  
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
  public Object execute(Object leftValue) {
    for (BaseNode node : children) {
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
