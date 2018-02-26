
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static rowdy.testlang.lang.RowdyGrammarConstants.ARITHM_LESSEQUAL;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.trimEmptyChildren;

/**
 *
 * @author Richard
 */
public class RelLessEqualTest extends TestCase {
  
  public RelLessEqualTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(RelLessEqualTest.class);
    return suite;
  }

  public void testExecute() {
    String testCode = "<= 2";
    Value leftValue = new Value(2, false);
    BaseNode instance = getTestStatement(testCode, ARITHM_LESSEQUAL);
    assertTrue(instance instanceof RelLessEqual);
    assertFalse(instance.isCompressable());
    trimEmptyChildren(instance);
    Value expResult = new Value(true, false);
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
  }
  
}
