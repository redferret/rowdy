 
package rowdy.nodes.expressions;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.BoolOr;
import static rowdy.testlang.lang.RowdyGrammarConstants.BOOL_EXPR;
import static rowdy.testlang.lang.RowdyGrammarConstants.BOOL_TERM_TAIL;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class BoolTermTailTest extends TestCase {
  
  public BoolTermTailTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(BoolTermTailTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class BoolOr.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "or (1 == 0)";
    BoolOr instance = (BoolOr) getTestStatement(testCode, BOOL_TERM_TAIL);
    Value expResult = new Value(true, false);
    Value result = instance.execute(new Value(true, false));
    assertEquals(expResult, result);
  }
  
}
