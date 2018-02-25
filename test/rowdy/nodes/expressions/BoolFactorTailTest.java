 
package rowdy.nodes.expressions;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.BoolAnd;
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
   * Test of execute method, of class BoolAnd.
   */
  public void testExecute() throws ConstantReassignmentException {
    Value leftValue = new Value(true, false);
    String testCode = "and 1 == 1";
    BoolAnd instance = (BoolAnd) getTestStatement(testCode, BOOL_FACTOR_TAIL);
    Value expResult = new Value(true, false);
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
  }
  
}
