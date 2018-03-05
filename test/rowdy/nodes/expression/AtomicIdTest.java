
package rowdy.nodes.expression;

import rowdy.BaseNode;
import rowdy.Value;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static rowdy.testlang.lang.RowdyGrammarConstants.ATOMIC_ID;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.rowdyInstance;
import static rowdy.testutils.TestUtils.trimEmptyChildren;

/**
 *
 * @author Richard
 */
public class AtomicIdTest {

  @Test
  public void testAtomicId() throws Throwable {
    String testCode = "var1";
    BaseNode instance = getTestStatement(testCode, ATOMIC_ID);
    trimEmptyChildren(instance);
    assertTrue(instance instanceof AtomicId);
    rowdyInstance.setAsGlobal("var1", new Value(99, false));
    Integer result = (Integer) instance.execute().getValue();
    Integer expected = 99;
    assertEquals(expected, result);
  }
  
}
