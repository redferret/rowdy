 
package rowdy.expressionNodeTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.BoolFactorTail;
import static rowdy.testlang.lang.RowdyGrammarConstants.BOOL_FACTOR_TAIL;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class BoolFactorTailTest extends TestCase {
  
  public BoolFactorTailTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(BoolFactorTailTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class BoolFactorTail.
   */
  public void testExecute() throws ConstantReassignmentException {
    Value leftValue = new Value(true);
    String testCode = "and 1 == 1";
    BoolFactorTail instance = (BoolFactorTail) getTestStatement(testCode, BOOL_FACTOR_TAIL);
    Value expResult = new Value(true);
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
  }
  
}
