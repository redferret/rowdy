 
package rowdy.expressionNodeTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.RelationOpt;
import static rowdy.testlang.lang.RowdyGrammarConstants.RELATION_OPTION;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.trimEmptyChildren;

/**
 *
 * @author Richard
 */
public class RelationOptTest extends TestCase {
  
  public RelationOptTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(RelationOptTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class RelationOpt.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = ">= 100";
    Value leftValue = new Value(1000);
    RelationOpt instance = (RelationOpt) getTestStatement(testCode, RELATION_OPTION);
    trimEmptyChildren(instance);
    Value expResult = new Value(true);
    Value result = instance.execute(leftValue);
    assertEquals(expResult, result);
  }
  
}
