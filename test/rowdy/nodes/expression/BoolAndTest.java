
package rowdy.nodes.expression;

import java.util.ArrayList;
import java.util.List;
import rowdy.Value;
import rowdy.BaseNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static rowdy.testlang.lang.RowdyGrammarConstants.BOOL_FACTOR_TAIL;
import static rowdy.testutils.TestUtils.getTestStatement;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;



/**
 *
 * @author Richard
 */
@RunWith(Parameterized.class)
public class BoolAndTest {
  
  private final String testCode;
  private final Value expectedResult;
  private final Value leftValue;
  
  public BoolAndTest(String testCode, Value leftValue, Value expectedResult) {
    this.testCode = testCode;
    this.expectedResult = expectedResult;
    this.leftValue = leftValue;
  }

  @Test
  public void testExecute() {
    BaseNode instance = getTestStatement(testCode, BOOL_FACTOR_TAIL);
    assertTrue(instance instanceof BoolAnd);
    assertTrue(instance.isCompressable());
    Value result = instance.execute(leftValue);
    assertEquals(expectedResult, result);
  }
  
  // Provide data
  @Parameterized.Parameters
  public static List<Object[]> data() {
    List<Object[]> list = new ArrayList<>();
    list.add(new Object[]{"and true", new Value(true, false), new Value(true, false)});
    list.add(new Object[]{"and true", new Value(false, false), new Value(false, false)});
    list.add(new Object[]{"and false", new Value(true, false), new Value(false, false)});
    list.add(new Object[]{"and false", new Value(false, false), new Value(false, false)});
    return list;
  }
  
}
