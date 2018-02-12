 
package rowdy.expressionNodeTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.FactorTail;

/**
 *
 * @author Richard
 */
public class FactorTailTest extends TestCase {
  
  public FactorTailTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(FactorTailTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class FactorTail.
   */
  public void testExecute() throws ConstantReassignmentException {
    System.out.println("execute");
    Value leftValue = null;
    FactorTail instance = null;
    Value expResult = null;
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
  
}
