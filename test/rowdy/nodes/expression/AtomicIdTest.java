
package rowdy.nodes.expression;


import rowdy.BaseNode;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.Value;
import org.junit.Test;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static rowdy.testlang.lang.RowdyGrammarConstants.ASSIGN_STMT;
import static rowdy.testlang.lang.RowdyGrammarConstants.ATOMIC_ID;
import static rowdy.testlang.lang.RowdyGrammarConstants.BOOL_EXPR;
import static rowdy.testlang.lang.RowdyGrammarConstants.CLASS_DEF;
import static rowdy.testlang.lang.RowdyGrammarConstants.DEFINITION;
import static rowdy.testlang.lang.RowdyGrammarConstants.STMT_LIST;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.rowdyInstance;

/**
 *
 * @author Richard
 */
public class AtomicIdTest {

  @Test
  public void testObjectAccess() throws Throwable {
    String testCode = "public class Person {" +
                        "	construct(name, age) {" +
                        "		this.name = name" +
                        "		this.age = age" +
                        "	}" +
                        "	public:" +
                        "		age = 0" +
                        "		func getName() {" +
                        "			return this.name" +
                        "		}" +
                        "	private:" +
                        "		name = \"\"" +
                        "}";
    BaseNode classDeclare = getTestStatement(testCode, DEFINITION);
    rowdyInstance.declareGlobals(classDeclare);
    String testCreateObject = "p = new Person(\"Test Name\", 32)";
    BaseNode createObject = getTestStatement(testCreateObject, STMT_LIST);
    rowdyInstance.executeStmt(createObject, null);
    
  }
  
  @Test
  public void testAtomicId() throws Throwable {
    String testCode = "var1";
    BaseNode instance = getTestStatement(testCode, ATOMIC_ID);
    assertTrue(instance instanceof AtomicId);
    rowdyInstance.setAsGlobal("var1", new Value(99, false));
    Integer result = (Integer) ((Value) instance.execute()).getValue();
    Integer expected = 99;
    assertEquals(expected, result);
  }
  
  @Test
  public void testNonArrayAccess()  throws ConstantReassignmentException {
    String testCode = "a = 15";
    String testDeref = "a[1]";
    BaseNode instance = getTestStatement(testCode, STMT_LIST);
    rowdyInstance.executeStmt(instance, null);
    BaseNode deref = getTestStatement(testDeref, ATOMIC_ID);
    
    try {
      deref.execute();
      fail("Expected RuntimeException to be thrown when trying to access non-array");
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is("Attempting to access a non-array 'a' on line 1"));
    }
  }
  
  @Test
  public void testInnerNonArrayAccess() throws ConstantReassignmentException {
    String testCode = "a = new [1, 2, 3]";
    String testDeref = "a[1][2]";
    BaseNode instance = getTestStatement(testCode, STMT_LIST);
    rowdyInstance.executeStmt(instance, null);
    BaseNode deref = getTestStatement(testDeref, ATOMIC_ID);
    
    try {
      deref.execute();
      fail("Expected RuntimeException to be thrown when trying to access non-array");
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is("Attempting to access a non-array 'a' indexed [1] on line 1"));
    }
  }
  @Test
  public void testMultiDimArray() throws ConstantReassignmentException {
    String testCode = "a = new [1, new [5, 6, 7], 3]";
    String testDeref = "a[1][2]";
    BaseNode instance = getTestStatement(testCode, STMT_LIST);
    rowdyInstance.executeStmt(instance, null);
    BaseNode deref = getTestStatement(testDeref, ATOMIC_ID);
    Integer result = (Integer) ((Value) deref.execute()).getValue();
    Integer expected = 7;
    assertEquals(expected, result);
  }
  @Test
  public void testArrayAccess() throws ConstantReassignmentException {
    String testCode = "a = new [1, 2, 3]";
    String testDeref = "a[1]";
    BaseNode instance = getTestStatement(testCode, STMT_LIST);
    rowdyInstance.executeStmt(instance, null);
    BaseNode deref = getTestStatement(testDeref, ATOMIC_ID);
    Integer result = (Integer) ((Value) deref.execute()).getValue();
    Integer expected = 2;
    assertEquals(expected, result);
  }
  
}
