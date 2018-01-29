
package rowdy;

import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.Test;
import static rowdy.Rowdy.*;


/**
 *
 * @author Richard
 */
public class RowdyBuilderTest {
  
  private static final RowdyLexer parser = 
          new RowdyLexer(TERMINALS, SPECIAL_SYMBOLS, ID, CONST);
  private static final Language rowdy = 
          Language.build(GRAMMAR, TERMINALS, NONTERMINALS);
  private static final RowdyBuilder builder = 
          RowdyBuilder.getBuilder(rowdy);
  
  /**
   * Test of getProgram method, of class RowdyBuilder.
   */
  @Test
  public void testGetProgramAsSingleLine() {
    
    String testProgram = "main = func(){}";
    
    parser.parseLine(testProgram);
    builder.buildAsSingleLine(parser);
    Node root = builder.getProgram();
    assertNotNull(root);
    String actual = root.symbol().getSymbol();
    String expected = "stmt-list";
    assertEquals(expected, actual);
    
    Node assignStmt = root.get(STATEMENT).get(ASSIGN_STMT);
    actual = assignStmt.symbol().getSymbol();
    expected = "assign-stmt";
    assertEquals(expected, actual);
  }

  @Test
  public void testGetProgramAsProgram() {
    String testProgram = "func main(){}";
    
    parser.parseLine(testProgram);
    builder.build(parser);
    Node root = builder.getProgram();
    assertNotNull(root);
    String actual = root.symbol().getSymbol();
    String expected = "prog";
    assertEquals("No program found", expected, actual);
    
    Node definition = root.get(DEFINITION);
    actual = definition.symbol().getSymbol();
    expected = "def";
    assertEquals("No definition found", expected, actual);
    
    Node function = definition.get(FUNCTION);
    actual = function.symbol().getSymbol();
    expected = "function";
    assertEquals("No function found", expected, actual);
    
    Node mainFunc = function.get(ID);
    actual = ((Terminal)mainFunc.symbol()).getName();
    expected = "main";
    assertEquals("No main found",expected, actual);
  }
  
  
  
}
