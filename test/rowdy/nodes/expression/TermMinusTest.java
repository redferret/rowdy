
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static rowdy.testlang.lang.RowdyGrammarConstants.TERM_MINUS;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class TermMinusTest extends TestCase {
  
  public TermMinusTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(TermMinusTest.class);
    return suite;
  }

  public void testExecute() {
    String testCode = "- 100";
    Value leftValue = new Value(100, false);
    BaseNode instance = getTestStatement(testCode, TERM_MINUS);
    assertTrue(instance instanceof TermMinus);
    Value expResult = new Value(0, false);
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
  }
  
  public void testNegativeValue() {
    String testCode = "- 100";
    BaseNode instance = getTestStatement(testCode, TERM_MINUS);
    assertTrue(instance instanceof TermMinus);
    Value expResult = new Value(-100, false);
    Value result = instance.execute();
    assertEquals(expResult, result);
  }
}
