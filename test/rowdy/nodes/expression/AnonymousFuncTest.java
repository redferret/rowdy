
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.testlang.lang.RowdyGrammarConstants.ANONYMOUS_FUNC;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class AnonymousFuncTest extends TestCase {
  
  public AnonymousFuncTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(AnonymousFuncTest.class);
    return suite;
  }

  public void testExecute() throws ConstantReassignmentException {
    String testCode = "func() {}";
    BaseNode func = getTestStatement(testCode, ANONYMOUS_FUNC);
    assertTrue(func instanceof AnonymousFunc);
    Value funcValue = (Value) func.execute();
    assertNotNull(funcValue);
    assertFalse(funcValue.isConstant());
  }
  
}
