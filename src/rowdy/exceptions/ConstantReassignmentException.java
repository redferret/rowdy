
package rowdy.exceptions;

/**
 *
 * @author Richard
 */
public class ConstantReassignmentException extends RuntimeException {

  public ConstantReassignmentException(String idName) {
    super(idName + " is a constant");
  }
  
  public ConstantReassignmentException(String idName, int lineNumber) {
    super(idName + " is a constant on line " + lineNumber);
  }
  
}
