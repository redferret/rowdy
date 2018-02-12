
package rowdy.expressionNodeTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.TermTail;
import static rowdy.testlang.lang.RowdyGrammarConstants.TERM_TAIL;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class TermTailTest extends TestCase {
  
  public TermTailTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(TermTailTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class TermTail.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "+ 100";
    Value leftValue = new Value(100);
    TermTail instance = (TermTail) getTestStatement(testCode, TERM_TAIL);
    Value expResult = new Value(200.0);
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
  }
  
}
