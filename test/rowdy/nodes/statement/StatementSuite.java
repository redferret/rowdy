
package rowdy.nodes.statement;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Richard
 */
public class StatementSuite extends TestCase {
  
  public StatementSuite(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite("StatementSuite");
    suite.addTest(ReturnStatementTest.suite());
    suite.addTest(IfStatementTest.suite());
    suite.addTest(PrintStatementTest.suite());
    suite.addTest(AssignStatementTest.suite());
    suite.addTest(LoopStatementTest.suite());
    suite.addTest(BreakStatementTest.suite());
    suite.addTest(ReadStatementTest.suite());
    return suite;
  }
  
}
