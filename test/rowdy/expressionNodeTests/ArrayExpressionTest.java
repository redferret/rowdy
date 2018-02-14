 
package rowdy.expressionNodeTests;

import java.util.Arrays;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.ArrayExpression;
import static rowdy.testlang.lang.RowdyGrammarConstants.ARRAY_EXPR;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class ArrayExpressionTest extends TestCase {
  
  public ArrayExpressionTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(ArrayExpressionTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class ArrayExpression.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "array(1, 2, 3)";
    ArrayExpression instance = (ArrayExpression) getTestStatement(testCode, ARRAY_EXPR);
    List<Object> expResult = Arrays.asList(1, 2, 3);
    List<Object> result = (List<Object>) instance.execute().getValue();
    
    for (int i = 0; i < 3; i++) {
      assertEquals(((Integer)expResult.get(i)), result.get(i));
    }
  }
  
}
