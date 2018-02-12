
package rowdy.expressionNodeTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.Term;

/**
 *
 * @author Richard
 */
public class TermTest extends TestCase {
  
  public TermTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(TermTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class Term.
   */
  public void testExecute() throws ConstantReassignmentException {
    System.out.println("execute");
    Value leftValue = null;
    Term instance = null;
    Value expResult = null;
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
  
}
