
package rowdy.nodes.expression;

import rowdy.BaseNode;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import rowdy.Value;
import static rowdy.testlang.lang.RowdyGrammarConstants.ATOMIC_FUNC_CALL;
import static rowdy.testlang.lang.RowdyGrammarConstants.STATEMENT;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.rowdyInstance;
import static rowdy.testutils.TestUtils.trimEmptyChildren;

/**
 *
 * @author Richard
 */
public class AtomicFuncCallTest {

  @Test
  public void testAtmoicFuncCallNoParams() throws Throwable {
    String testFuncCode = "f = func(){return 199 as int}";
    String testCall = "$f()";
    BaseNode funcNode = getTestStatement(testFuncCode, STATEMENT);
    trimEmptyChildren(funcNode);
    rowdyInstance.executeStmt(funcNode, null);
    BaseNode instance = getTestStatement(testCall, ATOMIC_FUNC_CALL);
    assertTrue(instance instanceof AtomicFuncCall);
    trimEmptyChildren(instance);
    Integer result = (Integer) ((Value) instance.execute()).getValue();
    Integer expected = 199;
    assertEquals(expected, result);
  }
  
  @Test
  public void testAtmoicFuncCallWithParams() throws Throwable {
    String testFuncCode = "f = func(a, b){return a + b as int}";
    String testCall = "$f(3, 4)";
    BaseNode funcNode = getTestStatement(testFuncCode, STATEMENT);
    trimEmptyChildren(funcNode);
    rowdyInstance.executeStmt(funcNode, null);
    BaseNode instance = getTestStatement(testCall, ATOMIC_FUNC_CALL);
    assertTrue(instance instanceof AtomicFuncCall);
    trimEmptyChildren(instance);
    Integer result = (Integer) ((Value) instance.execute()).getValue();
    Integer expected = 7;
    assertEquals(expected, result);
  }
  
}
