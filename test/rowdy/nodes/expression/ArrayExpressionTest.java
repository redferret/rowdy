 
package rowdy.nodes.expression;

import java.util.Arrays;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.testlang.lang.RowdyGrammarConstants.ARRAY_EXPR;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class ArrayExpressionTest extends TestCase {
  
  public ArrayExpressionTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(ArrayExpressionTest.class);
    return suite;
  }

  public void testExecuteArrayList() throws ConstantReassignmentException {
    String testCode = "[1, 2, 3]";
    BaseNode instance = getTestStatement(testCode, ARRAY_EXPR);
    assertTrue(instance instanceof ArrayExpression);
    List<Object> expResult = Arrays.asList(1, 2, 3);
    List<Object> result = (List<Object>) ((Value) instance.execute()).getValue();
    
    for (int i = 0; i < 3; i++) {
      assertEquals(((Integer)expResult.get(i)), result.get(i));
    }
  }
  
  public void testEmptyArray() throws ConstantReassignmentException {
    String testCode = "[]";
    BaseNode instance = getTestStatement(testCode, ARRAY_EXPR);
    assertTrue(instance instanceof ArrayExpression);
    List<Object> result = (List<Object>) ((Value) instance.execute()).getValue();
    assertTrue(result.isEmpty());
  }
  
//  public void testExecuteHashMap() throws ConstantReassignmentException {
//    String testCode = "{1:\"A\", 2:\"B\", 3:\"C\"}";
//    BaseNode instance = getTestStatement(testCode, ARRAY_EXPR);
//    assertTrue(instance instanceof ArrayExpression);
//    List<Object> expResult = Arrays.asList("A", "B", "C");
//    HashMap<String, Object> result = (HashMap<String, Object>) instance.execute().getValue();
//    
//    for (int i = 1; i <= 3; i++) {
//      assertEquals(expResult.get(i-1), result.get(Integer.toString(i)));
//    }
//  }
  
}
