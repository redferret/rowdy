
package rowdy.nodes.expression;

import junit.framework.TestCase;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.testlang.lang.RowdyGrammarConstants.EXPRESSIONS;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class ExpressionsTest extends TestCase {
  
  public ExpressionsTest(String testName) {
    super(testName);
  }

  /**
   * Test of execute method, of class Expressions.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "50 + 6";
    Expressions instance = (Expressions) getTestStatement(testCode, EXPRESSIONS);
    Double expResult = 56.0;
    Double result = instance.execute().valueToDouble();
    assertEquals(expResult, result);
  }
  
}
