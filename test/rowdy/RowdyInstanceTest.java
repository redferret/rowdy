
package rowdy;

import junit.framework.TestCase;
import static rowdy.testlang.lang.RowdyGrammarConstants.EXPRESSION;
import static rowdy.testutils.TestUtils.getTestStatement;
import static rowdy.testutils.TestUtils.rowdyInstance;

/**
 *
 * @author Richard
 */
public class RowdyInstanceTest extends TestCase {
  
  public RowdyInstanceTest(String testName) {
    super(testName);
  }

  public void testInitialize() {
  }

  public void testCompress() {
    String testCode = "1 > 1";
    BaseRowdyNode testProgram = getTestStatement(testCode, EXPRESSION);
    System.out.println(testProgram);
    rowdyInstance.compress(testProgram);
    System.out.println(testProgram);
  }

  public void testDumpCallStack() {
  }

  public void testExecuteLine() throws Exception {
  }

  public void testDeclareGlobals_0args() throws Exception {
  }

  public void testExecute() throws Exception {
  }

  public void testDeclareSystemConstants() throws Exception {
  }

  public void testDeclareGlobals_RowdyNode() throws Exception {
  }

  public void testExecuteStmt() throws Exception {
  }

  public void testExecuteFunc_RowdyNode() throws Exception {
  }

  public void testExecuteFunc_Node_List() throws Exception {
  }

  public void testExecuteFunc_3args() throws Exception {
  }

  public void testAllocateIfExists() {
  }

  public void testAllocate() throws Exception {
  }

  public void testSetAsGlobal_String_Value() throws Exception {
  }

  public void testSetAsGlobal_Terminal_Value() throws Exception {
  }

  public void testGetIdAsValue() {
  }

  public void testIsset() {
  }

  public void testFetch() {
  }

  public void testFetchInCallStack() {
  }

  public void testCollect() {
  }

  public void testCollectTerminals() {
  }

  public void testPrint_0args() {
  }

  public void testPrint_Node() {
  }
  
}
