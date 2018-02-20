
package rowdy.nodes.statements;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.statement.BreakStatement;
import rowdy.nodes.statement.LoopStatement;
import static rowdy.testlang.lang.RowdyGrammarConstants.BREAK_STMT;
import static rowdy.testlang.lang.RowdyGrammarConstants.LOOP_STMT;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.rowdyInstance;

/**
 *
 * @author Richard
 */
public class BreakStatementTest extends TestCase {
  
  public BreakStatementTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(BreakStatementTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class BreakStatement.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "break";
    
    try {
      BreakStatement instance = (BreakStatement) getTestStatement(testCode, BREAK_STMT);
      instance.execute();
      fail("Expected to fail with no loop");
    } catch (Throwable e) {}
    
    rowdyInstance.globalSymbolTable.put("x", new Value(0, false));
    String loopCode = "loop x: {}";
    LoopStatement loopStmt = (LoopStatement) getTestStatement(loopCode, LOOP_STMT);
    rowdyInstance.activeLoops.push(loopStmt);
    
    testCode = "break x";
    BreakStatement instance = (BreakStatement) getTestStatement(testCode, BREAK_STMT);
    instance.execute();
    
    assertTrue(rowdyInstance.activeLoops.isEmpty());
    assertNull(rowdyInstance.globalSymbolTable.get("x"));
  }
  
}
