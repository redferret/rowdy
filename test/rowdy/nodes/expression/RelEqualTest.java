
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.ARITHM_EQUAL;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.trimEmptyChildren;

/**
 *
 * @author Richard
 */
public class RelEqualTest extends TestCase {
  
  public RelEqualTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(RelEqualTest.class);
    return suite;
  }

  public void testExecute() {
    String testCode = "== 2";
    Value leftValue = new Value(2, false);
    BaseNode instance = getTestStatement(testCode, ARITHM_EQUAL);
    assertTrue(instance instanceof RelEqual);
    assertFalse(instance.isCompressable());
    trimEmptyChildren(instance);
    Value expResult = new Value(true, false);
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
  }
  
}
