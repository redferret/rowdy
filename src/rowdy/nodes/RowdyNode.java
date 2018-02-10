
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
  
  protected RowdyRunner runner;
  
  public RowdyNode(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber);
    this.runner = runner;
  }
  
  public final Value execute() throws ConstantReassignmentException {
    return execute(this, null);
  }
  
  public final Value execute(Value leftValue) throws ConstantReassignmentException {
    return execute(this, leftValue);
  }
  
  /**
   * Override this method to implement your logic
   * @param cur
   * @param leftValue
   * @return
   * @throws ConstantReassignmentException 
   */
  public Value execute(RowdyNode cur, Value leftValue) throws ConstantReassignmentException {
    return leftValue;
  }
}
