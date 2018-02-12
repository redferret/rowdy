 
package rowdy.expressionNodeTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.BoolTerm;
import static rowdy.testlang.lang.RowdyGrammarConstants.BOOL_EXPR;
import static rowdy.testlang.lang.RowdyGrammarConstants.BOOL_TERM;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class BoolTermTest extends TestCase {
  
  public BoolTermTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(BoolTermTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class BoolTerm.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "(1 == 1) or (1 == 0)";
    BoolTerm instance = (BoolTerm) getTestStatement(testCode, BOOL_EXPR).get(BOOL_TERM);
    Value expResult = new Value(true);
    Value result = instance.execute();
    assertEquals(expResult, result);
  }
  
}
