 
package rowdy.expressionNodeTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.BoolTermTail;

/**
 *
 * @author Richard
 */
public class BoolTermTailTest extends TestCase {
  
  public BoolTermTailTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(BoolTermTailTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class BoolTermTail.
   */
  public void testExecute() throws ConstantReassignmentException {
    System.out.println("execute");
    Value leftValue = null;
    BoolTermTail instance = null;
    Value expResult = null;
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
  
}
