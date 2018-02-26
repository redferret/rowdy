
package rowdy.nodes.statement;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.statement.ReadStatement;
import static rowdy.testlang.lang.RowdyGrammarConstants.READ_STMT;
import static rowdy.testutils.TestUtils.fetch;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.isset;

/**
 *
 * @author Richard
 */
public class ReadStatementTest extends TestCase {
  
  public ReadStatementTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(ReadStatementTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class ReadStatement.
   */
  public void testExecute() throws ConstantReassignmentException {
    String testCode = "read v1, v2, v3";
    ReadStatement instance = (ReadStatement) getTestStatement(testCode, READ_STMT);
    
    String inputString = "56\n50\n42\n";
    InputStream stream = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));
    instance.execute(new Value(stream, false));
    
    String[] varsList = {"v1", "v2", "v3"};
    Integer[] vals = {56, 50, 42};
    for (int i = 0; i < 3; i++) {
      assertTrue(isset(varsList[i]));
      Integer expected = vals[i];
      Integer actual = Integer.parseInt(fetch(varsList[i]).getValue().toString());
      assertEquals(expected, actual);
    }
  }
}
