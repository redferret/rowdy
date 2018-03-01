
package rowdy.nodes.expression;

import java.util.ArrayList;
import java.util.List;
import rowdy.BaseNode;
import rowdy.Value;
import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;
import org.junit.Test;
import static rowdy.testlang.lang.RowdyGrammarConstants.BOOL_TERM_TAIL;
import static rowdy.testutils.TestUtils.getTestStatement;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Richard
 */
@RunWith(Parameterized.class)
public class BoolOrTest {
  
  private final String testCode;
  private final Value expectedResult;
  private final Value leftValue;
  
  public BoolOrTest(String testCode, Value leftValue, Value expectedResult) {
    this.testCode = testCode;
    this.expectedResult = expectedResult;
    this.leftValue = leftValue;
  }

  @Test
  public void testExecute() {
    BaseNode instance = getTestStatement(testCode, BOOL_TERM_TAIL);
    assertTrue(instance instanceof BoolOr);
    assertFalse(instance.isCompressable());
    Value result = instance.execute(leftValue);
    assertEquals(expectedResult, result);
  }
  
  // Provide data
  @Parameterized.Parameters
  public static List<Object[]> data() {
    List<Object[]> list = new ArrayList<>();
    list.add(new Object[]{"or true", new Value(true, false), new Value(true, false)});
    list.add(new Object[]{"or true", new Value(false, false), new Value(true, false)});
    list.add(new Object[]{"or false", new Value(true, false), new Value(true, false)});
    list.add(new Object[]{"or false", new Value(false, false), new Value(false, false)});
    return list;
  }
}
