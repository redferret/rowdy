
package rowdy;

import org.junit.Test;
import static rowdy.Rowdy.*;
import static rowdy.testUtils.TestUtils.*;
import static org.junit.Assert.*;

/**
 *
 * @author Richard
 */
public class RowdyBuilderTest {
  
  /**
   * Test of getProgram method, of class RowdyBuilder.
   */
  @Test
  public void testGetProgramAsSingleLine() {
    
    String testCode = "main = func(){}";
    
    Node root = getRoot(testCode);
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
    String testCode = "func main(){}";
    
    parser.parseLine(testCode);
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
  public void testAssignStatement() {
    String testCode = "a = 100";
    Node assignStmt = getTestStatement(testCode, ASSIGN_STMT);
    
    getAndTestSymbol(assignStmt, ID, "ID");
    getAndTestSymbol(assignStmt, BECOMES, "=");
    getAndTestSymbol(assignStmt, EXPRESSION, "expr");
  }
  
  @Test
  public void testLoopStatement() {
    String testCode = "loop x: {}";
    Node loopStmt = getTestStatement(testCode, LOOP_STMT);
    
    getAndTestSymbol(loopStmt, LOOP, "loop");
    getAndTestSymbol(loopStmt, ID, "ID");
    getAndTestSymbol(loopStmt, COLON, ":");
    getAndTestSymbol(loopStmt, STMT_BLOCK, "stmt-block");
  }
  
  @Test
  public void testIfStatement() {
    String testCode = "if true {} else {}";
    Node ifStmt = getTestStatement(testCode, IF_STMT);
    
    getAndTestSymbol(ifStmt, IF, "if");
    getAndTestSymbol(ifStmt, EXPRESSION, "expr");
    getAndTestSymbol(ifStmt, STMT_BLOCK, "stmt-block");
    getAndTestSymbol(ifStmt, ELSE_PART, "else-part");
  }
  
  @Test
  public void testBreakStatement() {
    String testCode = "break x";
    Node breakStmt = getTestStatement(testCode, BREAK_STMT);
    
    getAndTestSymbol(breakStmt, BREAK, "break");
    Node idOpt = getAndTestSymbol(breakStmt, ID_OPTION, "id-option");
    Node id = getAndTestSymbol(idOpt, ID, "ID");
    testForTerminal(id, "x");
  }
  
  @Test
  public void testReturnStatement() {
    String testCode = "return 100";
    Node returnStmt = getTestStatement(testCode, RETURN_STMT);
    
    getAndTestSymbol(returnStmt, RETURN, "return");
    getAndTestSymbol(returnStmt, EXPRESSION, "expr");
  }
  
  @Test
  public void testFunctionCallStatement() {
    String testCode = "->function()";
    Node functionCall = getTestStatement(testCode, FUNC_CALL);
    
    getAndTestSymbol(functionCall, CALL, "->");
    Node id = getAndTestSymbol(functionCall, ID, "ID");
    testForTerminal(id, "function");
    getAndTestSymbol(functionCall, OPENPAREN, "(");
    getAndTestSymbol(functionCall, EXPRESSION, "expr");
    getAndTestSymbol(functionCall, EXPR_LIST, "expr-list");
    getAndTestSymbol(functionCall, CLOSEDPAREN, ")");
  }
  
  @Test
  public void testReadStatement() {
    String testCode = "read testID";
    Node readStmt = getTestStatement(testCode, READ_STMT);
    
    getAndTestSymbol(readStmt, READ, "read");
    Node id = getAndTestSymbol(readStmt, ID, "ID");
    testForTerminal(id, "testID");
    getAndTestSymbol(readStmt, PARAMS_TAIL, "params-tail");
  }
  
  @Test
  public void testPrintStatement() {
    String testCode = "print testID";
    Node printStmt = getTestStatement(testCode, PRINT_STMT);
    
    getAndTestSymbol(printStmt, PRINT, "print");
    getAndTestSymbol(printStmt, EXPRESSION, "expr");
    getAndTestSymbol(printStmt, EXPR_LIST, "expr-list");
  }
  
}
