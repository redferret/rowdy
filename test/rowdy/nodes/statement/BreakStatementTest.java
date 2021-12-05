
package rowdy.nodes.statement;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Function;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
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
    
    String loopCode = "loop x: 0 {}";
    LoopStatement loopStmt = (LoopStatement) getTestStatement(loopCode, LOOP_STMT);
    Function f = rowdyInstance.callStack.peek();
    f.getSymbolTable().allocate("x", new Value(0), 1, true);
    
    f.activeLoops.push(loopStmt);
    
    testCode = "break x";
    BreakStatement instance = (BreakStatement) getTestStatement(testCode, BREAK_STMT);
    instance.execute();
    
    assertTrue(f.activeLoops.isEmpty());
    assertNull(rowdyInstance.globalSymbolTable.get("x"));
  }
  
}
