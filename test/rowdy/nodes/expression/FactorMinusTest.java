
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static rowdy.testlang.lang.RowdyGrammarConstants.FACTOR_MINUS;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.trimEmptyChildren;

/**
 *
 * @author Richard
 */
public class FactorMinusTest extends TestCase {
  
  public FactorMinusTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(FactorMinusTest.class);
    return suite;
  }

  public void testExecute() {
    String testCode = "- 2";
    Value leftValue = new Value(40, false);
    BaseNode instance = getTestStatement(testCode, FACTOR_MINUS);
    assertTrue(instance instanceof FactorMinus);
    assertFalse(instance.isCompressable());
    trimEmptyChildren(instance);
    Value expResult = new Value(38, false);
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
  }
  
  public void testNegativeValue() {
    String testCode = "- 100";
    BaseNode instance = getTestStatement(testCode, FACTOR_MINUS);
    assertTrue(instance instanceof FactorMinus);
    Value expResult = new Value(-100, false);
    Value result = instance.execute();
    assertEquals(expResult, result);
  }
}
