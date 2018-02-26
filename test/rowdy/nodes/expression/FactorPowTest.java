
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static rowdy.testlang.lang.RowdyGrammarConstants.FACTOR_TAIL_POW;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.trimEmptyChildren;

/**
 *
 * @author Richard
 */
public class FactorPowTest extends TestCase {
  
  public FactorPowTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(FactorPowTest.class);
    return suite;
  }

  public void testExecute() {
    String testCode = "^ 2";
    Value leftValue = new Value(2, false);
    BaseNode instance = getTestStatement(testCode, FACTOR_TAIL_POW);
    assertTrue(instance instanceof FactorPow);
    assertFalse(instance.isCompressable());
    trimEmptyChildren(instance);
    Value expResult = new Value(4.0, false);
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
  }
  
}
