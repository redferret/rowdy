
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
    String actual = root.symbol().getSymbolAsString();
    String expected = "stmt-list";
    assertEquals(expected, actual);
    
    Node assignStmt = root.get(STATEMENT).get(ASSIGN_STMT);
    actual = assignStmt.symbol().getSymbolAsString();
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
    String actual = root.symbol().getSymbolAsString();
    String expected = "prog";
    assertEquals("No program found", expected, actual);
    
    Node definition = getAndTestSymbol(root, DEFINITION, "def");
    Node function = getAndTestSymbol(definition, FUNCTION, "function");
    
    Node mainFunc = getFromAndTestNotNull(function, ID);
    testForTerminal(mainFunc, "main");
  }
  
  @Test
  public void testAssignmentStatement() {
    String testProgram = "a = 100";
    
    parser.parseLine(testProgram);
    builder.buildAsSingleLine(parser);
    Node root = builder.getProgram();
    assertNotNull(root);
    
    Node statement = getFromAndTestNotNull(root, STATEMENT);
    Node assignStmt = getFromAndTestNotNull(statement, ASSIGN_STMT);
    
    getAndTestSymbol(assignStmt, ID, "ID");
    getAndTestSymbol(assignStmt, BECOMES, "=");
    getAndTestSymbol(assignStmt, EXPRESSION, "expr");
  }
  
  private Node getFromAndTestNotNull(Node from, int id) {
    Node toFetch = from.get(id, false);
    assertNotNull(toFetch);
    return toFetch;
  }
  
  private void testForTerminal(Node terminal, String expected) {
    assertNotNull(terminal);
    String actual = ((Terminal)terminal.symbol()).getName();
    assertEquals("No main found",expected, actual);
  }
  
  private Node getAndTestSymbol(Node from, int nodeId, String expected){
    Node toFetch = getFromAndTestNotNull(from, nodeId);
    String actual = toFetch.symbol().getSymbolAsString();
    assertEquals(expected, actual);
    return toFetch;
  }
  
}
