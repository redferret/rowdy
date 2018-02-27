
package rowdy.nodes.expression;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import rowdy.BaseNode;
import rowdy.Value;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static rowdy.testlang.lang.RowdyGrammarConstants.ARITHM_NOTEQUAL;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.trimEmptyChildren;

/**
 *
 * @author Richard
 */
@RunWith(Parameterized.class)
public class RelNotEqualTest {
  
  private final String testCode;
  private final Value expectedResult;
  private final Value leftValue;
  
  public RelNotEqualTest(String testCode, Value leftValue, Value expectedResult) {
    this.testCode = testCode;
    this.expectedResult = expectedResult;
    this.leftValue = leftValue;
  }

  @Test
  public void testExecute() {
    BaseNode instance = getTestStatement(testCode, ARITHM_NOTEQUAL);
    assertTrue(instance instanceof RelNotEqual);
    assertFalse(instance.isCompressable());
    trimEmptyChildren(instance);
    Value result = instance.execute(leftValue);
    assertEquals(expectedResult, result);
  }
  // Provide data
  @Parameterized.Parameters
  public static List<Object[]> data() {
    List<Object[]> list = new ArrayList<>();
    list.add(new Object[]{"!= 100", new Value(50, false), new Value(true, false)});
    list.add(new Object[]{"!= 100", new Value(100, false), new Value(false, false)});
    list.add(new Object[]{"!= 50", new Value(100, false), new Value(true, false)});
    return list;
  }
}
