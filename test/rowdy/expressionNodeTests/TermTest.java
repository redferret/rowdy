
package rowdy.expressionNodeTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.Term;
import static rowdy.testlang.lang.RowdyGrammarConstants.TERM;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class TermTest extends TestCase {
  
  public TermTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(TermTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class Term.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "1000";
    Term instance = (Term) getTestStatement(testCode, TERM);
    Double expResult = new Value(1000).valueToDouble();
    Double result = instance.execute().valueToDouble();
    assertEquals(expResult, result);
  }
  
}
