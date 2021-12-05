
package rowdy.nodes.statement;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Function;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.testlang.lang.RowdyGrammarConstants.LOOP_STMT;
import static rowdy.testutils.TestUtils.fetch;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.rowdyInstance;

/**
 *
 * @author Richard
 */
public class LoopStatementTest extends TestCase {
  
  public LoopStatementTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(LoopStatementTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class LoopStatement.
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "loop x:0 {a = 12 break}";
    LoopStatement loopStmt = (LoopStatement) getTestStatement(testCode, LOOP_STMT);
    
    loopStmt.execute();
    Value aVal = fetch("a");
    Integer result = (Integer) aVal.getValue();
    Integer expected = 12;
    assertEquals(expected, result);
  }
  
}
