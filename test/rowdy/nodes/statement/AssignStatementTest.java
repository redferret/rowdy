
package rowdy.nodes.statement;

import java.util.HashMap;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Function;
import rowdy.RowdyObject;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.testlang.lang.RowdyGrammarConstants.ASSIGN_STMT;
import static rowdy.testutils.TestUtils.fetch;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.isset;
import static rowdy.testutils.TestUtils.rowdyInstance;

/**
 *
 * @author Richard
 */
public class AssignStatementTest extends TestCase {
  
  public AssignStatementTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(AssignStatementTest.class);
    return suite;
  }

  public void testExecute() throws ConstantReassignmentException {
    String testCode = "a = 10 as int";
    AssignStatement instance = (AssignStatement) getTestStatement(testCode, ASSIGN_STMT);
    instance.execute();
    Boolean result = isset("a");
    assertTrue("The ID 'a' doesn't exist", result);
    Integer expected = 10;
    Integer actual = (Integer) fetch("a").getValue();
    assertEquals(expected, actual);
  }
  
  public void testConstModifier() throws Throwable {
    String testCode = "const a = 10 as int";
    AssignStatement instance = (AssignStatement) getTestStatement(testCode, ASSIGN_STMT);
    instance.execute();
    Boolean result = isset("a");
    assertTrue("The ID 'a' doesn't exist", result);
    Value constValue = fetch("a");
    Integer expected = 10;
    Integer actual = (Integer) constValue.getValue();
    assertEquals(expected, actual);
    assertTrue(constValue.isConstant());
  }
  
  public void testThisConstModifier() throws Throwable {
    String testCode = "const this.B = 10";
    AssignStatement instance = (AssignStatement) getTestStatement(testCode, ASSIGN_STMT);
    Function function = new Function("Test", new HashMap<>(), 0);
    rowdyInstance.callStack.push(function);
    instance.execute();
    Value value = function.getSymbolTable().getValue("B");
    assertNotNull(value);
    assertTrue(value.isConstant());
  }
  
  public void testGlobalModifier() throws Throwable {
    String testCode = "global c = 10";
    AssignStatement instance = (AssignStatement) getTestStatement(testCode, ASSIGN_STMT);
    instance.execute();
    Value value = rowdyInstance.globalSymbolTable.get("c");
    assertNotNull(value);
  }
  
  public void testThisModifierOnFunction() throws Throwable {
    String testCode = "this.d = 10";
    AssignStatement instance = (AssignStatement) getTestStatement(testCode, ASSIGN_STMT);
    Function function = new Function("Test", new HashMap<>(), 0);
    rowdyInstance.callStack.push(function);
    instance.execute();
    Value value = function.getSymbolTable().getValue("d");
    assertNotNull(value);
  }
  
  public void testThisModifierOnObject() throws Throwable {
    String testCode = "this.G = 10";
    AssignStatement instance = (AssignStatement) getTestStatement(testCode, ASSIGN_STMT);
    Function function = new Function("Test", new HashMap<>(), 0);
    RowdyObject parent = new RowdyObject("Test Class");
    function.setClassObject(parent);
    rowdyInstance.callStack.push(function);
    instance.execute();
    Value value = function.getSymbolTable().getValue("G");
    assertNull(value);
    value = function.getClassObject().getSymbolTable().getValue("G");
    assertNotNull(value);
  }
  
//  public void testIncrementTest() {
//    String testCode = "var1 = 0";
//    AssignStatement instance = (AssignStatement) getTestStatement(testCode, ASSIGN_STMT);
//    instance.execute();
//    testCode = "var1++";
//    instance = (AssignStatement) getTestStatement(testCode, ASSIGN_STMT);
//    instance.execute();
//    Boolean result = isset("var1");
//    assertTrue("The ID 'var1' doesn't exist", result);
//    Integer expected = 1;
//    Integer actual = (Integer) fetch("var1").getValue();
//    assertEquals(expected, actual);
//  }
//  
//  public void testDecrementTest() {
//    String testCode = "var1 = 1";
//    AssignStatement instance = (AssignStatement) getTestStatement(testCode, ASSIGN_STMT);
//    instance.execute();
//    testCode = "var1--";
//    instance = (AssignStatement) getTestStatement(testCode, ASSIGN_STMT);
//    instance.execute();
//    Boolean result = isset("var1");
//    assertTrue("The ID 'var1' doesn't exist", result);
//    Integer expected = 0;
//    Integer actual = (Integer) fetch("var1").getValue();
//    assertEquals(expected, actual);
//  }
  
}
