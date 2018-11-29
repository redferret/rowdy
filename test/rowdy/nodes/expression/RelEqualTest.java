
package rowdy.nodes.expression;

import java.util.ArrayList;
import java.util.List;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import rowdy.BaseNode;
import rowdy.Value;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static rowdy.lang.RowdyGrammarConstants.ARITHM_EQUAL;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
@RunWith(Parameterized.class)
public class RelEqualTest {
     
  private final String testCode;
  private final Value expectedResult;
  private final Value leftValue;
  
  public RelEqualTest(String testCode, Value leftValue, Value expectedResult) {
    this.testCode = testCode;
    this.expectedResult = expectedResult;
    this.leftValue = leftValue;
  }

  @org.junit.Test
  public void testExecute() {
    BaseNode instance = getTestStatement(testCode, ARITHM_EQUAL);
    assertTrue(instance instanceof RelEqual);
    assertFalse(instance.isCompressable());
    Value result = instance.execute(leftValue);
    assertEquals(expectedResult, result);
  }
  // Provide data
  @Parameterized.Parameters
  public static List<Object[]> data() {
    List<Object[]> list = new ArrayList<>();
    list.add(new Object[]{"== \"Hello\"", new Value("Hello", false), new Value(false, false)});
    list.add(new Object[]{"== 1.0", new Value(1, false), new Value(true, false)});
    list.add(new Object[]{"== 1", new Value(1.0, false), new Value(true, false)});
    list.add(new Object[]{"== 1.0", new Value(1.0, false), new Value(true, false)});
    list.add(new Object[]{"== 100", new Value(50, false), new Value(false, false)});
    list.add(new Object[]{"== 100", new Value(150, false), new Value(false, false)});
    list.add(new Object[]{"== 100", new Value(100, false), new Value(true, false)});
    list.add(new Object[]{"== true", new Value(true, false), new Value(true, false)});
    list.add(new Object[]{"== true", new Value(false, false), new Value(false, false)});
    list.add(new Object[]{"== null", new Value(true, false), new Value(false, false)});
    list.add(new Object[]{"== null", new Value(false, false), new Value(false, false)});
    list.add(new Object[]{"== null", new Value(null, false), new Value(true, false)});
    list.add(new Object[]{"== 0", new Value(false, false), new Value(true, false)});
    list.add(new Object[]{"== 1", new Value(true, false), new Value(true, false)});
    return list;
  }
}
