/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.trimEmptyChildren;
import static junit.framework.TestCase.assertEquals;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_TAIL_MUL;

/**
 *
 * @author Richard
 */
public class FactorMulTest extends TestCase {
  
  public FactorMulTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(FactorMulTest.class);
    return suite;
  }

  public void testExecute() {
    String testCode = "* 5";
    Value leftValue = new Value(4, false);
    BaseNode instance = getTestStatement(testCode, FACTOR_TAIL_MUL);
    assertTrue(instance instanceof FactorMul);
    assertFalse(instance.isCompressable());
    trimEmptyChildren(instance);
    Value expResult = new Value(20, false);
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
  }
  
}
