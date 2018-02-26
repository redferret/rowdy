/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rowdy.nodes.expression;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Richard
 */
public class ExpressionSuite extends TestCase {
  
  public ExpressionSuite(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite("ExpressionSuite");
    suite.addTest(FactorPowTest.suite());
    suite.addTest(TermMinusTest.suite());
    suite.addTest(IssetExprTest.suite());
    suite.addTest(RelLessEqualTest.suite());
    suite.addTest(RelEqualTest.suite());
    suite.addTest(BoolOrTest.suite());
    suite.addTest(AnonymousFuncTest.suite());
    suite.addTest(FactorModTest.suite());
    suite.addTest(AtomicTest.suite());
    suite.addTest(ExpressionTest.suite());
    suite.addTest(FactorMinusTest.suite());
    suite.addTest(FactorDivTest.suite());
    suite.addTest(RoundExprTest.suite());
    suite.addTest(TermPlusTest.suite());
    suite.addTest(BoolAndTest.suite());
    suite.addTest(RelLessTest.suite());
    suite.addTest(ConcatExprTest.suite());
    suite.addTest(FactorMulTest.suite());
    suite.addTest(RelGreaterTest.suite());
    suite.addTest(ArrayExpressionTest.suite());
    suite.addTest(RelGreaterEqualTest.suite());
    return suite;
  }
  
}
