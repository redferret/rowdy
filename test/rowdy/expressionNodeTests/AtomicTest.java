
package rowdy.expressionNodeTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.Atomic;
import static rowdy.testlang.lang.RowdyGrammarConstants.ATOMIC;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.trimEmptyChildren;

/**
 *
 * @author Richard
 */
public class AtomicTest extends TestCase {
  
  public AtomicTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(AtomicTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class Atomic.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "600";
    Atomic instance = (Atomic) getTestStatement(testCode, ATOMIC);
    trimEmptyChildren(instance);
    Double expResult = 600.0;
    Double result = instance.execute().valueToDouble();
    assertEquals(expResult, result);
  }
  
}
