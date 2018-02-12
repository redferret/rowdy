
package rowdy;

import org.junit.Test;
import growdy.Node;
import static org.junit.Assert.*;
import static growdy.testUtils.TestUtils.*;
import static rowdy.testlang.lang.RowdyGrammarConstants.*;
import static rowdy.testutils.TestUtils.getTestStatement;
/**
 *
 * @author Richard
 */
public class RowdyBuilderStmtTest {
  
  @Test
  public void testStmtBlock() {
    String testCode = "func main(){}";
    
    Node function = getTestStatement(testCode, FUNCTION);
    Node functionBody = getFromAndTestNotNull(function, FUNCTION_BODY);
    Node stmtBlock = getFromAndTestNotNull(functionBody, STMT_BLOCK);
    testContainsSymbols(stmtBlock, new int[]{LCURLY, STMT_LIST, RCURLY});
    
  }

  @Test
  public void testAssignStatement() {
    String testCode = "a = 100";
    Node assignStmt = getTestStatement(testCode, ASSIGN_STMT);
    
    getAndTestSymbol(assignStmt, ID, "id");
    getAndTestSymbol(assignStmt, BECOMES, "=");
    testContainsSymbols(assignStmt, new int[]{CONST_OPT, ID, BECOMES, EXPRESSION});
    
    testCode = "const a = 100";
    assignStmt = getTestStatement(testCode, ASSIGN_STMT);
    Node constOpt = getFromAndTestNotNull(assignStmt, CONST_OPT);
    assertFalse(constOpt.getAll().isEmpty());
    getAndTestSymbol(constOpt, CONST_DEF, "const");
    
  }
  
  @Test
  public void testLoopStatement() {
    String testCode = "loop x: {}";
    Node loopStmt = getTestStatement(testCode, LOOP_STMT);
    
    getAndTestSymbol(loopStmt, LOOP, "loop");
    getAndTestSymbol(loopStmt, COLON, ":");
    testContainsSymbols(loopStmt, new int[]{LOOP, ID, COLON, STMT_BLOCK});
  }
  
  @Test
  public void testIfStatement() {
    String testCode = "if true {} else {}";
    Node ifStmt = getTestStatement(testCode, IF_STMT);
    
    getAndTestSymbol(ifStmt, IF, "if");
    getAndTestSymbol(ifStmt, ELSE_PART, "else-part");
    
    testContainsSymbols(ifStmt, new int[]{IF, EXPRESSION, STMT_BLOCK, ELSE_PART});
    Node elsePart = getFromAndTestNotNull(ifStmt, ELSE_PART);
    testContainsSymbols(elsePart, new int[]{ELSE, STMT_BLOCK});
    
    testCode = "if (some == yes) {}";
    ifStmt = getTestStatement(testCode, IF_STMT);
    
    getFromAndTestNotNull(ifStmt, IF);
    getFromAndTestNotNull(ifStmt, EXPRESSION);
    getFromAndTestNotNull(ifStmt, STMT_BLOCK);
    elsePart = getFromAndTestNotNull(ifStmt, ELSE_PART);
    
    assertTrue(elsePart.getAll().isEmpty());
    
  }
  
  @Test
  public void testBreakStatement() {
    String testCode = "break x";
    Node breakStmt = getTestStatement(testCode, BREAK_STMT);
    
    testContainsSymbols(breakStmt, new int[]{BREAK, ID_OPTION});
    getAndTestSymbol(breakStmt, BREAK, "break");
    Node idOpt = getAndTestSymbol(breakStmt, ID_OPTION, "id-option");
    Node id = getFromAndTestNotNull(idOpt, ID);
    testForTerminal(id, "x");
    
    testCode = "break";
    breakStmt = getTestStatement(testCode, BREAK_STMT);
    idOpt = getFromAndTestNotNull(breakStmt, ID_OPTION);
    assertTrue(idOpt.getAll().isEmpty());
  }
  
  @Test
  public void testReturnStatement() {
    String testCode = "return 100";
    Node returnStmt = getTestStatement(testCode, RETURN_STMT);
    
    testContainsSymbols(returnStmt, new int[]{RETURN, EXPRESSION});
    getAndTestSymbol(returnStmt, RETURN, "return");
  }
  
  @Test
  public void testFunctionCallStatement() {
    String testCode = "$function()";
    Node functionCall = getTestStatement(testCode, FUNC_CALL);
    
    getAndTestSymbol(functionCall, CALL, "$");
    Node id = getFromAndTestNotNull(functionCall, ID);
    testForTerminal(id, "function");
    getAndTestSymbol(functionCall, OPENPAREN, "(");
    getAndTestSymbol(functionCall, CLOSEDPAREN, ")");
    
    testContainsSymbols(functionCall, 
            new int[]{CALL, ID, OPENPAREN, EXPRESSION, EXPR_LIST, CLOSEDPAREN});
    
    testCode = "$function(0, \"Hello\", apples)";
    getTestStatement(testCode, FUNC_CALL);
  }
  
  @Test
  public void testReadStatement() {
    String testCode = "read testID";
    Node readStmt = getTestStatement(testCode, READ_STMT);
    
    getAndTestSymbol(readStmt, READ, "read");
    Node id = getFromAndTestNotNull(readStmt, ID);
    testForTerminal(id, "testID");
    
    testContainsSymbols(readStmt, new int[]{READ, ID, PARAMS_TAIL});
    
    testCode = "read a1, a2, a3, a4, a5";
    readStmt = getTestStatement(testCode, READ_STMT);
    
    Node paramsTail = getFromAndTestNotNull(readStmt, PARAMS_TAIL);
    while(paramsTail.hasSymbols()) {
      getAndTestSymbol(paramsTail, COMMA, ",");
      getFromAndTestNotNull(paramsTail, ID);
      paramsTail = getFromAndTestNotNull(paramsTail, PARAMS_TAIL);
    }
  }
  
  @Test
  public void testPrintStatement() {
    String testCode = "print testID";
    Node printStmt = getTestStatement(testCode, PRINT_STMT);
    
    getAndTestSymbol(printStmt, PRINT, "print");
    testContainsSymbols(printStmt, new int[]{PRINT, EXPRESSION, EXPR_LIST});
    
    testCode = "print a1, a2, a3, a4, a5, 100, \"Hello World!\"";
    getTestStatement(testCode, PRINT_STMT);
    
  }
  
}
