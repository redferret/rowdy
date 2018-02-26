
package rowdy.nodes.statement;

import growdy.Node;
import java.util.HashMap;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Function;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import rowdy.nodes.statement.ReturnStatement;
import static rowdy.testlang.lang.RowdyGrammarConstants.RETURN_STMT;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.rowdyInstance;

/**
 *
 * @author Richard
 */
public class ReturnStatementTest extends TestCase {
  
  public ReturnStatementTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(ReturnStatementTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class ReturnStatement.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "return (1 + 1) as int";
    ReturnStatement returnStmt = (ReturnStatement) getTestStatement(testCode, RETURN_STMT);
    
    Function testFunction = new Function("Test", new HashMap<>(), 0);
    rowdyInstance.callStack.push(testFunction);
    BaseNode seqControl = new RowdyNode(null, 0);
    seqControl.setSeqActive(true);
    Value seqControlWrapper = new Value(seqControl, false);
    
    returnStmt.execute(seqControlWrapper);
    Boolean actual = seqControl.isSeqActive();
    assertFalse("Sequence Control is still active", actual);
    
    Value functionReturnVal = testFunction.getReturnValue();
    Integer result = (Integer) functionReturnVal.getValue();
    Integer expected = 2;
    assertEquals("Return value not set", expected, result);
  }
  
}
