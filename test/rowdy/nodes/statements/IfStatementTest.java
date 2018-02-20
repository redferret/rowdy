
package rowdy.nodes.statements;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.statement.IfStatement;
import static rowdy.testlang.lang.RowdyGrammarConstants.IF_STMT;
import static rowdy.testutils.TestUtils.getTestStatement;
import static junit.framework.TestCase.assertTrue;
import static rowdy.testutils.TestUtils.fetch;
import static rowdy.testutils.TestUtils.isset;
/**
 *
 * @author Richard
 */
public class IfStatementTest extends TestCase {
  
  public IfStatementTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(IfStatementTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class IfStatement.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "if 0 == 0 {a = 1 as int} else {a = 0 as int}";
    
    IfStatement instance = (IfStatement) getTestStatement(testCode, IF_STMT);
    
    Value seqControlWrapper = new Value(null, false);
    instance.execute(seqControlWrapper);
    
    assertTrue("The ID 'a' doesn't exist", isset("a"));
    
    Value varResult = fetch("a");
    Integer actual = (Integer) varResult.getValue();
    Integer expected = 1;
    assertEquals("ID 'a' not set correctly", expected, actual);
  }
  
}
