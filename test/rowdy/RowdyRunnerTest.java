
package rowdy;

import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @author Richard
 */
public class RowdyRunnerTest {
  
  private static final Language testLanguage = 
          Language.build(Rowdy.GRAMMAR, Rowdy.TERMINALS, Rowdy.NONTERMINALS);
  private static final RowdyRunner rowdyTestProgram = 
          new RowdyRunner();
  private static final RowdyLexer testParser = 
          new RowdyLexer(Rowdy.TERMINALS, Rowdy.SPECIAL_SYMBOLS,Rowdy.ID, Rowdy.CONST);
  private static final RowdyBuilder testBuilder = RowdyBuilder.getBuilder(testLanguage);
  
  private void buildAndParse(String code) {
    try {
      testParser.parseLine(code);
      testBuilder.build(testParser);
    }catch (Exception e){
      System.out.println("Build Exception: " + e.getMessage());
      fail("Build Exception");
    }
  }
  
  @After
  public void tearDown(){
    
  }
  @Test
  public void testPRULE_START(){
    
  }

  /**
   * Test of initialize method, of class RowdyRunner.
   */
  public void testInitialize() {
  }



  /**
   * Test of execute method, of class RowdyRunner.
   */
  public void testExecute() throws Exception {
  }

  /**
   * Test of executeStmt method, of class RowdyRunner.
   */
  public void testExecuteStmt() {
  }

  /**
   * Test of executeFunc method, of class RowdyRunner.
   */
  public void testExecuteFunc() {
  }

  /**
   * Test of allocate method, of class RowdyRunner.
   */
  public void testAllocate() {
  }

  /**
   * Test of setAsGlobal method, of class RowdyRunner.
   */
  public void testSetAsGlobalStringValue() {
  }

  /**
   * Test of setAsGlobal method, of class RowdyRunner.
   */
  public void testSetAsGlobalTerminalValue() {
  }

  /**
   * Test of isset method, of class RowdyRunner.
   */
  public void testIssetNode() {
  }

  /**
   * Test of isset method, of class RowdyRunner.
   */
  public void testIssetValue() {
  }

  /**
   * Test of getValue method, of class RowdyRunner.
   */
  public void testGetValue() {
  }

  /**
   * Test of fetch method, of class RowdyRunner.
   */
  public void testFetch() {
//    System.out.println("fetch");
//    Value value = null;
//    Node curSeq = null;
//    RowdyRunner instance = new RowdyRunner();
//    Value expResult = null;
//    Value result = instance.fetch(value, curSeq);
//    assertEquals(expResult, result);
//    // TODO review the generated test code and remove the default call to fail.
//    fail("The test case is a prototype.");
  }

  /**
   * Test of executeExpr method, of class RowdyRunner.
   */
  public void testExecuteExpr() {
//    System.out.println("executeExpr");
//    Node cur = null;
//    Value leftValue = null;
//    RowdyRunner instance = new RowdyRunner();
//    Value expResult = null;
//    Value result = instance.executeExpr(cur, leftValue);
//    assertEquals(expResult, result);
//    // TODO review the generated test code and remove the default call to fail.
//    fail("The test case is a prototype.");
  }

}
