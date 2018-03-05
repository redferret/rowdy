
package rowdy.nodes.expression;

import rowdy.BaseNode;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static rowdy.testlang.lang.RowdyGrammarConstants.ATOMIC_CONST;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.trimEmptyChildren;

/**
 *
 * @author Richard
 */
public class AtomicConstTest {

  @Test
  public void testAtomicConst() throws Throwable {
    String testCode = "600";
    BaseNode instance = getTestStatement(testCode, ATOMIC_CONST);
    assertTrue(instance instanceof AtomicConst);
    trimEmptyChildren(instance);
    Double expResult = 600.0;
    Double result = instance.execute().valueToDouble();
    assertEquals(expResult, result);
  }
  
}
