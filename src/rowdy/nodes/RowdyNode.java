
package rowdy.nodes;

import growdy.Node;
import growdy.Symbol;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;


/**
 *
 * @author Richard
 */
public class RowdyNode extends Node {
  
  protected static RowdyRunner runner;
  
  public RowdyNode(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  
  public final Value execute() throws ConstantReassignmentException {
    return execute(null);
  }
  
  public static void initRunner(RowdyRunner runner) {
    RowdyNode.runner = runner;
  }
  
  /**
   * Override this method to implement your logic
   * @param cur
   * @param leftValue
   * @return
   * @throws ConstantReassignmentException 
   */
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    return leftValue;
  }
}
