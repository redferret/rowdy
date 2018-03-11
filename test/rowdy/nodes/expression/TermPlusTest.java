
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import static junit.framework.TestCase.assertEquals;
import static rowdy.testlang.lang.RowdyGrammarConstants.TERM_PLUS;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class TermPlusTest extends TestCase {
  
  public TermPlusTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(TermPlusTest.class);
    return suite;
  }

  public void testExecute() {
    String testCode = "+ 100";
    Value leftValue = new Value(100, false);
    BaseNode instance = getTestStatement(testCode, TERM_PLUS);
    assertTrue(instance instanceof TermPlus);
    Value expResult = new Value(200, false);
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
  }
  
}
