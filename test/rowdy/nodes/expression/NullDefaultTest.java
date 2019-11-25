
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.testlang.lang.RowdyGrammarConstants.EXPRESSION;
import static rowdy.testlang.lang.RowdyGrammarConstants.STMT_LIST;
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
    BaseNode instance = getTestStatement(testCode, EXPRESSION);
    boolean result = (boolean) ((Value) instance.execute()).getValue();
    assertTrue(result);
    
    rowdyInstance.allocate("testVar1", new Value(10, false), 0);
    
    result = ! (boolean) ((Value) instance.execute()).getValue();
    assertTrue(result);
  }
  
  public void testForDefaultValue() {
    String testCode = "a = is testVar2? \"default\"";
    BaseNode instance = getTestStatement(testCode, STMT_LIST);
    instance.execute();
    Value value = rowdyInstance.globalSymbolTable.get("a");
    String result = (String) value.getValue();
    String expected = "default";
    assertEquals(expected, result);
    
    getTestStatement("testVar2 = \"Test\"", STMT_LIST).execute();
    instance.execute();
    value = rowdyInstance.globalSymbolTable.get("a");
    result = (String) value.getValue();
    expected = "Test";
    assertEquals(expected, result);
  }
  
}
