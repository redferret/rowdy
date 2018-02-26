/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rowdy.nodes;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.nodes.expression.ExpressionSuite;
import rowdy.nodes.statement.StatementSuite;

/**
 *
 * @author Richard
 */
public class NodesSuite extends TestCase {
  
  public NodesSuite(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite("NodesSuite");
    suite.addTest(RowdyNodeTest.suite());
    suite.addTest(StatementSuite.suite());
    suite.addTest(ExpressionSuite.suite());
    suite.addTest(RowdyNodeFactoryTest.suite());
    return suite;
  }
  
}
