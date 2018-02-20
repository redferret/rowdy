 
package rowdy.nodes.expressions;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.Factor;
import static rowdy.testlang.lang.RowdyGrammarConstants.FACTOR;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.trimEmptyChildren;

/**
 *
 * @author Richard
 */
public class FactorTest extends TestCase {
  
  public FactorTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(FactorTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class Factor.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "100";
    Factor instance = (Factor) getTestStatement(testCode, FACTOR);
    trimEmptyChildren(instance);
    Double expResult = 100.0;
    Double result = instance.execute().valueToDouble();
    assertEquals(expResult, result);
  }
  
}
