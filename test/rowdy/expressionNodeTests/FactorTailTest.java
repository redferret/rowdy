 
package rowdy.expressionNodeTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.FactorTail;
import static rowdy.testlang.lang.RowdyGrammarConstants.FACTOR_TAIL;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.trimEmptyChildren;

/**
 *
 * @author Richard
 */
public class FactorTailTest extends TestCase {
  
  public FactorTailTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(FactorTailTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class FactorTail.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "* 5";
    Value leftValue = new Value(4);
    FactorTail instance = (FactorTail) getTestStatement(testCode, FACTOR_TAIL);
    trimEmptyChildren(instance);
    Value expResult = new Value(20.0);
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
  }
  
}
