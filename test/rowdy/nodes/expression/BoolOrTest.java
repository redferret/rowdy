
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.testlang.lang.RowdyGrammarConstants.BOOL_TERM_TAIL;
import static rowdy.testutils.TestUtils.getTestStatement;
import static junit.framework.TestCase.assertEquals;
/**
 *
 * @author Richard
 */
public class BoolOrTest extends TestCase {
  
  public BoolOrTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(BoolOrTest.class);
    return suite;
  }

  public void testExecute() {
    String testCode = "or (1 == 0)";
    BaseNode instance = getTestStatement(testCode, BOOL_TERM_TAIL);
    assertTrue(instance instanceof BoolOr);
    Value expResult = new Value(true, false);
    Value result = instance.execute(new Value(true, false));
    assertEquals(expResult, result);
  }
  
}
