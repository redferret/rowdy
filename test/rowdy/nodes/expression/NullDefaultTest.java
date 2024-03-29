
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.statement.AssignStatement;
import static rowdy.testlang.lang.RowdyGrammarConstants.ASSIGN_STMT;
import static rowdy.testlang.lang.RowdyGrammarConstants.NULL_DEFAULT;
import static rowdy.testutils.TestUtils.fetch;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.rowdyInstance;

/**
 *
 * @author Richard
 */
public class NullDefaultTest extends TestCase  {
  
  public NullDefaultTest(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite(NullDefaultTest.class);
    return suite;
  }
  
  public void testForNullOnly() throws ConstantReassignmentException {
    String testCode = "is testVar1?";
    NullDefault instance = (NullDefault) getTestStatement(testCode, NULL_DEFAULT);
    boolean result = (boolean) ((Value) instance.execute()).getValue();
    assertTrue(result);
    
    rowdyInstance.allocate("testVar1", new Value(10, false), 0);
    
    result = ! (boolean) ((Value) instance.execute()).getValue();
    assertTrue(result);
  }
  
  public void testForDefaultValue() {
    String testCode = "a = is testVar2? \"default\"";
    AssignStatement instance = (AssignStatement) getTestStatement(testCode, ASSIGN_STMT);
    instance.execute();
    Value value = fetch("a");
    String result = (String) value.getValue();
    String expected = "default";
    assertEquals(expected, result);
    
    ((AssignStatement) getTestStatement("testVar2 = \"Test\"", ASSIGN_STMT)).execute();
    instance.execute();
    value = fetch("a");
    result = (String) value.getValue();
    expected = "Test";
    assertEquals(expected, result);
  }
  
}
