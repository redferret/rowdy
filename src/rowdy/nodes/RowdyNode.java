
package rowdy.nodes;

import growdy.Node;
import growdy.Symbol;
import rowdy.RowdyInstance;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;


/**
 *
 * @author Richard
 */
public class RowdyNode extends Node {
  
  protected static RowdyInstance instance;
  
  public RowdyNode(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  
  public final Value execute() throws ConstantReassignmentException {
    return execute(null);
  }
  
  public static void initRunner(RowdyInstance runner) {
    RowdyNode.instance = runner;
  }
  
  /**
   * Override this method to implement your logic
   * @param cur
   * @param leftValue
   * @return
   * @throws ConstantReassignmentException 
   */
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    return instance.fetch(leftValue, this);
  }
}
