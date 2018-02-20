
package rowdy.nodes.statements;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.statement.AssignStatement;
import static rowdy.testlang.lang.RowdyGrammarConstants.ASSIGN_STMT;
import static rowdy.testutils.TestUtils.fetch;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.isset;

/**
 *
 * @author Richard
 */
public class AssignStatementTest extends TestCase {
  
  public AssignStatementTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(AssignStatementTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class AssignStatement.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "a = 10 as int";
    AssignStatement instance = (AssignStatement) getTestStatement(testCode, ASSIGN_STMT);
    instance.execute();
    Boolean result = isset("a");
    assertTrue("The ID 'a' doesn't exist", result);
    Integer expected = 10;
    Integer actual = (Integer) fetch("a").getValue();
    assertEquals(expected, actual);
  }
  
}
