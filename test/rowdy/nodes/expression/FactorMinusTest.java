/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import junit.framework.TestSuite;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_TAIL_DIV;
import static rowdy.testlang.lang.RowdyGrammarConstants.FACTOR_MINUS;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.trimEmptyChildren;

/**
 *
 * @author Richard
 */
public class FactorMinusTest extends TestCase {
  
  public FactorMinusTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(FactorMinusTest.class);
    return suite;
  }

  public void testExecute() {
    String testCode = "- 2";
    Value leftValue = new Value(40, false);
    BaseNode instance = getTestStatement(testCode, FACTOR_MINUS);
    assertTrue(instance instanceof FactorMinus);
    assertFalse(instance.isCompressable());
    trimEmptyChildren(instance);
    Value expResult = new Value(38.0, false);
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
  }
  
}
