 
package rowdy.nodes.expressions;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.Expression;
import static rowdy.testlang.lang.RowdyGrammarConstants.EXPRESSION;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.trimEmptyChildren;

/**
 *
 * @author Richard
 */
public class ExpressionTest extends TestCase {
  
  public ExpressionTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(ExpressionTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class Expression.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "3 + 8 - 9 * 19 >= 100";
    Expression instance = (Expression) getTestStatement(testCode, EXPRESSION);
    trimEmptyChildren(instance);
    Value expResult = new Value(false, false);
    Value result = instance.execute();
    assertEquals(expResult, result);
  }
  
}
