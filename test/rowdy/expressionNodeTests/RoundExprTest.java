
package rowdy.expressionNodeTests;

import growdy.Terminal;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.RoundExpr;
import static rowdy.testlang.lang.RowdyGrammarConstants.ID;
import static rowdy.testlang.lang.RowdyGrammarConstants.ROUND_EXPR;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.rowdyInstance;

/**
 *
 * @author Richard
 */
public class RoundExprTest extends TestCase {
  
  public RoundExprTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(RoundExprTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class RoundExpr.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "round a, 1";
    RoundExpr instance = (RoundExpr) getTestStatement(testCode, ROUND_EXPR);
    rowdyInstance.allocate((Terminal) instance.get(ID).symbol(), new Value(123.87));
    Value expResult = new Value(123.9);
    Value result = instance.execute();
    assertEquals(expResult, result);
  }
  
}
