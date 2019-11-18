 
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.testlang.lang.RowdyGrammarConstants.EXPRESSION;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class ExpressionTest extends TestCase {
  
  public ExpressionTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(ExpressionTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class Expression.
   */
  public void testExecute() {
    String testCode = "3 + 8 - 9 * 19 >= 100";
    BaseNode instance = getTestStatement(testCode, EXPRESSION);
    Value expResult = new Value(false, false);
    Value result = (Value) instance.execute();
    assertEquals(expResult, result);
  }
  
}
