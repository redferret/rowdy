
package rowdy.nodes.statements;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.statement.PrintStatement;
import static rowdy.testlang.lang.RowdyGrammarConstants.PRINT_STMT;
import static rowdy.testutils.TestUtils.getTestStatement;

/**
 *
 * @author Richard
 */
public class PrintStatementTest extends TestCase {
  
  public PrintStatementTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(PrintStatementTest.class);
    return suite;
  }

  /**
   * Test of execute method, of class PrintStatement.
   */
  public void testExecute() throws ConstantReassignmentException, UnsupportedEncodingException {
    String testCode = "print \"Hello World!\", (2 * 2) as int";

    PrintStatement instance = (PrintStatement) getTestStatement(testCode, PRINT_STMT);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    try (PrintStream printStream = new PrintStream(baos, true, "utf-8")) {
      instance.execute(new Value(printStream, false));
      String result = new String(baos.toByteArray(), StandardCharsets.UTF_8);
      String expected = "Hello World!4";
      assertEquals(expected, result);
    }
  }
  
}
