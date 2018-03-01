
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import rowdy.nodes.RowdyNode;
import static rowdy.testlang.lang.RowdyGrammarConstants.ATOMIC;
import static rowdy.testlang.lang.RowdyGrammarConstants.FUNC_CALL;
import static rowdy.testlang.lang.RowdyGrammarConstants.STATEMENT;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.rowdyInstance;
import static rowdy.testutils.TestUtils.trimEmptyChildren;

/**
 *
 * @author Richard
 */
public class AtomicTest extends TestCase {
  
  public AtomicTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(AtomicTest.class);
    return suite;
  }

  public void testAtomicConst() throws Throwable {
    String testCode = "600";
    BaseNode instance = getTestStatement(testCode, ATOMIC);
    assertTrue(instance instanceof Atomic);
    trimEmptyChildren(instance);
    Double expResult = 600.0;
    Double result = instance.execute().valueToDouble();
    assertEquals(expResult, result);
  }
  
  public void testAtomicId() throws Throwable {
    String testCode = "var1";
    BaseNode instance = getTestStatement(testCode, ATOMIC);
    trimEmptyChildren(instance);
    assertTrue(instance instanceof Atomic);
    rowdyInstance.setAsGlobal("var1", new Value(99, false));
    Integer result = (Integer) instance.execute().getValue();
    Integer expected = 99;
    assertEquals(expected, result);
  }
  
  public void testAtmoicFuncCall() throws Throwable {
    String testFuncCode = "f = func(){return 199 as int}";
    String testCall = "$f()";
    BaseNode funcNode = getTestStatement(testFuncCode, STATEMENT);
    trimEmptyChildren(funcNode);
    rowdyInstance.executeStmt(funcNode, null);
    BaseNode instance = getTestStatement(testCall, ATOMIC);
    assertTrue(instance instanceof Atomic);
    trimEmptyChildren(instance);
    Integer result = (Integer) instance.execute().getValue();
    Integer expected = 199;
    assertEquals(expected, result);
  }

}
