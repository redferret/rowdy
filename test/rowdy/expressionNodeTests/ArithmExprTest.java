 
package rowdy.expressionNodeTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.ArithmExpr;
import static rowdy.testlang.lang.RowdyGrammarConstants.ARITHM_EXPR;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class ArithmExprTest extends TestCase {
  
  public ArithmExprTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(ArithmExprTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class ArithmExpr.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "5 + 9 - 8";
    ArithmExpr instance = (ArithmExpr) getTestStatement(testCode, ARITHM_EXPR);
    Value expResult = new Value(6.0, false);
    Value result = instance.execute();
    assertEquals(expResult, result);
  }
  
}
