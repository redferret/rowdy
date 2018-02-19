 
package rowdy.nodes.expressions;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.BoolFactor;
import static rowdy.testlang.lang.RowdyGrammarConstants.BOOL_FACTOR;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class BoolFactorTest extends TestCase {
  
  public BoolFactorTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(BoolFactorTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class BoolFactor.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "1 + 1 > 0 + 1";
    BoolFactor instance = (BoolFactor) getTestStatement(testCode, BOOL_FACTOR);
    Value expResult = new Value(true, false);
    Value result = instance.execute();
    assertEquals(expResult, result);
  }
  
}
