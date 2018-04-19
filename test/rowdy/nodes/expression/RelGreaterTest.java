
package rowdy.nodes.expression;

import java.util.ArrayList;
import java.util.List;
import rowdy.BaseNode;
import rowdy.Value;
import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static rowdy.testlang.lang.RowdyGrammarConstants.ARITHM_GREATER;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
@RunWith(Parameterized.class)
public class RelGreaterTest {
  
  private final String testCode;
  private final Value expectedResult;
  private final Value leftValue;
  
  public RelGreaterTest(String testCode, Value leftValue, Value expectedResult) {
    this.testCode = testCode;
    this.expectedResult = expectedResult;
    this.leftValue = leftValue;
  }

  @org.junit.Test
  public void testExecute() {
    BaseNode instance = getTestStatement(testCode, ARITHM_GREATER);
    assertTrue(instance instanceof RelGreater);
    assertFalse(instance.isCompressable());
    Value result = instance.execute(leftValue);
    assertEquals(expectedResult, result);
  }
  // Provide data
  @Parameterized.Parameters
  public static List<Object[]> data() {
    List<Object[]> list = new ArrayList<>();
    list.add(new Object[]{"> 100", new Value(50, false), new Value(false, false)});
    list.add(new Object[]{"> 100", new Value(150, false), new Value(true, false)});
    list.add(new Object[]{"> 100", new Value(100, false), new Value(false, false)});
    return list;
  }
}
