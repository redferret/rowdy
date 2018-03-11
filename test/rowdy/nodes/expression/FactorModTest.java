
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.testlang.lang.RowdyGrammarConstants.FACTOR_TAIL_MOD;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.trimEmptyChildren;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 *
 * @author Richard
 */
public class FactorModTest extends TestCase {
  
  public FactorModTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(FactorModTest.class);
    return suite;
  }

  public void testExecute() {
    String testCode = "% 2";
    Value leftValue = new Value(40, false);
    BaseNode instance = getTestStatement(testCode, FACTOR_TAIL_MOD);
    assertTrue(instance instanceof FactorMod);
    trimEmptyChildren(instance);
    Value expResult = new Value(0, false);
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
  }
  
}
