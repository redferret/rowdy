
package rowdy;

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

}
