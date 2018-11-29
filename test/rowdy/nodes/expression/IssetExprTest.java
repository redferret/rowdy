
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.testlang.lang.RowdyGrammarConstants.ISSET_EXPR;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.rowdyInstance;

/**
 *
 * @author Richard
 */
public class IssetExprTest extends TestCase {
  
  public IssetExprTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(IssetExprTest.class);
    return suite;
  }

  public void testExistsExecute() throws ConstantReassignmentException {
    String testCode = "isset AAA";
    rowdyInstance.setAsGlobal("AAA", new Value(0, false));
    BaseNode instance = getTestStatement(testCode, ISSET_EXPR);
    assertTrue(instance instanceof IssetExpr);
    Value expected = new Value(true, false);
    assertEquals(expected, instance.execute());
  }
  
  public void testNotExistsExecute() throws ConstantReassignmentException {
    String testCode = "isset BBB";
    BaseNode instance = getTestStatement(testCode, ISSET_EXPR);
    assertTrue(instance instanceof IssetExpr);
    Value expected = new Value(false, false);
    assertEquals(expected, instance.execute());
  }
  
}
