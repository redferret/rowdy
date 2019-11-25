
package rowdy.nodes.statement;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.testlang.lang.RowdyGrammarConstants.IF_STMT;
import static rowdy.testlang.lang.RowdyGrammarConstants.LOOP_STMT;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.fetch;
import static rowdy.testutils.TestUtils.isset;
import static junit.framework.TestCase.assertTrue;
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
  public void testForTrueAndFalseOnIfElse() throws ConstantReassignmentException {
    String testCode = "if 0 == 0 {a = 1 as int} else {a = 0 as int}";
    
    IfStatement instance = (IfStatement) getTestStatement(testCode, IF_STMT);
    
    instance.execute(null);
    
    assertTrue("The ID 'a' doesn't exist", isset("a"));
    
    Value varResult = fetch("a");
    Integer actual = (Integer) varResult.getValue();
    Integer expected = 1;
    assertEquals("ID 'a' not set correctly", expected, actual);
  }
  
  public void testFullIfElseIf() throws ConstantReassignmentException {
    String testCode = 
            "  loop test: {"
            + " if (test == 0) {"
            + "   a = 0 as int"
            + " } else if (test == 1) {"
            + "   b = 1 as int"
            + " } else {"
            + "   c = 2"
            + "   break test"
            + " } "
            + " test = test + 1"
            + "}";
    
    LoopStatement instance = (LoopStatement) getTestStatement(testCode, LOOP_STMT);
    
    instance.execute();
    
    String[] vars = {"a", "b", "c"};
    Integer[] expectedValues = {0, 1, 2};
    
    for (int i = 0; i < 3; i++) {
      assertTrue("Variable '"+vars[i]+"' should exist", isset(vars[i]));
      
      Value varResult = fetch(vars[i]);
      Integer actual = (Integer) varResult.getValue();
      Integer expected = expectedValues[i];
      assertEquals("ID '"+vars[i]+"' not set correctly", expected, actual);
    }
    
  }
  
}
