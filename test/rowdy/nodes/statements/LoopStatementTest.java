
package rowdy.nodes.statements;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.statement.LoopStatement;
import static rowdy.testlang.lang.RowdyGrammarConstants.LOOP_STMT;
import static rowdy.testutils.TestUtils.fetch;
import static rowdy.testutils.TestUtils.getTestStatement;

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
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "loop x: {a = x as int break}";
    LoopStatement instance = (LoopStatement) getTestStatement(testCode, LOOP_STMT);
    
    instance.execute();
    Value aVal = fetch("a");
    Integer result = (Integer) aVal.getValue();
    Integer expected = 0;
    assertEquals(expected, result);
  }
  
}
