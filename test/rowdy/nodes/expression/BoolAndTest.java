
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.BaseNode;
import static rowdy.testlang.lang.RowdyGrammarConstants.BOOL_FACTOR_TAIL;
import static rowdy.testutils.TestUtils.getTestStatement;
import static junit.framework.TestCase.assertEquals;


/**
 *
 * @author Richard
 */
public class BoolAndTest extends TestCase {
  
  public BoolAndTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(BoolAndTest.class);
    return suite;
  }

  public void testExecute() {
    Value leftValue = new Value(true, false);
    String testCode = "and 1 == 1";
    BaseNode instance = getTestStatement(testCode, BOOL_FACTOR_TAIL);
    assertTrue(instance instanceof BoolAnd);
    Value expResult = new Value(true, false);
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
  }
  
}
