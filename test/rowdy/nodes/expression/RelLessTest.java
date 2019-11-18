
package rowdy.nodes.expression;

import java.util.ArrayList;
import java.util.List;
import rowdy.BaseNode;
import rowdy.Value;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static rowdy.testlang.lang.RowdyGrammarConstants.ARITHM_LESS;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
@RunWith(Parameterized.class)
public class RelLessTest {
  
  private final String testCode;
  private final Value expectedResult;
  private final Value leftValue;
  
  public RelLessTest(String testCode, Value leftValue, Value expectedResult) {
    this.testCode = testCode;
    this.expectedResult = expectedResult;
    this.leftValue = leftValue;
  }

  @Test
  public void testExecute() {
    BaseNode instance = getTestStatement(testCode, ARITHM_LESS);
    assertTrue(instance instanceof RelLess);
    assertFalse(instance.isCompressable());
    Value result = (Value) instance.execute(leftValue);
    assertEquals(expectedResult, result);
  }
  // Provide data
  @Parameterized.Parameters
  public static List<Object[]> data() {
    List<Object[]> list = new ArrayList<>();
    list.add(new Object[]{"< 100", new Value(50, false), new Value(true, false)});
    list.add(new Object[]{"< 100", new Value(150, false), new Value(false, false)});
    list.add(new Object[]{"< 100", new Value(100, false), new Value(false, false)});
    return list;
  }
}
