
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.testlang.lang.RowdyGrammarConstants.CONCAT_EXPR;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class ConcatExprTest extends TestCase {
  
  public ConcatExprTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(ConcatExprTest.class);
    return suite;
  }

  public void testExecute() {
    String testCode = "concat \"Test\", \" \", \"Concat\"";
    BaseNode instance = getTestStatement(testCode, CONCAT_EXPR);
    assertTrue(instance instanceof ConcatExpr);
    String result = instance.execute().getValue().toString();
    String expected = new Value("Test Concat", false).getValue().toString();
    assertEquals(expected, result);
  }
  
}
