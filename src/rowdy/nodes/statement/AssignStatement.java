
package rowdy.nodes.statement;

import growdy.Symbol;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class AssignStatement extends RowdyNode {
  
  public AssignStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    
    return null;
  }
  
}
