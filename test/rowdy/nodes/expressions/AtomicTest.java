
package rowdy.nodes.expressions;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.nodes.RowdyNode;
import rowdy.nodes.expression.Atomic;
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
    Atomic instance = (Atomic) getTestStatement(testCode, ATOMIC);
    trimEmptyChildren(instance);
    Double expResult = 600.0;
    Double result = instance.execute().valueToDouble();
    assertEquals(expResult, result);
  }
  
  public void testAtomicId() throws Throwable {
    String testCode = "var1";
    Atomic instance = (Atomic) getTestStatement(testCode, ATOMIC);
    rowdyInstance.setAsGlobal("var1", new Value(99, false));
    Integer result = (Integer) instance.execute().getValue();
    Integer expected = 99;
    assertEquals(expected, result);
  }
  
  public void testAtmoicFuncCall() throws Throwable {
    String testFuncCode = "f = func(){return 199 as int}";
    String testCall = "$f()";
    RowdyNode funcNode = (RowdyNode) getTestStatement(testFuncCode, STATEMENT);
    trimEmptyChildren(funcNode);
    rowdyInstance.executeStmt(funcNode, null);
    RowdyNode funcCall = (RowdyNode) getTestStatement(testCall, FUNC_CALL);
    Integer result = (Integer) rowdyInstance.executeFunc(funcCall).getValue();
    Integer expected = 199;
    assertEquals(expected, result);
  }
}
