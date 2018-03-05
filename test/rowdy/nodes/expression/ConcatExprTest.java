
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.testlang.lang.RowdyGrammarConstants.EXPRESSION;
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

  public void testExecute() throws ConstantReassignmentException {
    String testCode = "concat \"Test\", \" \", \"Concat\"";
    BaseNode instance = getTestStatement(testCode, EXPRESSION);
    String result = instance.execute().getValue().toString();
    String expected = "Test Concat";
    assertEquals(expected, result);
  }
  
}
