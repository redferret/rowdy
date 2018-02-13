
package rowdy;

import growdy.Node;
import org.junit.Test;
import static rowdy.testlang.lang.RowdyGrammarConstants.*;
import static growdy.testUtils.TestUtils.*;
import static org.junit.Assert.*;
import static rowdy.testutils.TestUtils.getTestStatement;
/**
 *
 * @author Richard DeSilvey
 */
public class RowdyBuilderFuncTest {
  
  @Test
  public void testFunction() {
    String testCode = "func name(a, b){}";
    
    Node function = getTestStatement(testCode, FUNCTION);
    assertTrue("Function shouldn't be empty", function.hasSymbols());
    
    getAndTestSymbol(function, FUNC, "func");
    getFromAndTestNotNull(function, ID);
    Node functionBody = getAndTestSymbol(function, FUNCTION_BODY, "function-body");
    
    testContainsSymbols(functionBody, 
            new int[]{OPENPAREN, PARAMETERS, CLOSEDPAREN, STMT_BLOCK});
    Node parameters = getAndTestSymbol(functionBody, PARAMETERS, "parameters");
    testContainsSymbols(parameters, new int[]{ID, PARAMS_TAIL});
    Node paramTail = getAndTestSymbol(parameters, PARAMS_TAIL, "params-tail");
    testContainsSymbols(paramTail, new int[]{COMMA, ID, PARAMS_TAIL});
  }
  
  @Test
  public void testFunctionCall() {
    String testCode = "$fun(1, 2, A)";
    
    Node funcCall = getTestStatement(testCode, FUNC_CALL);
    testContainsSymbols(funcCall, 
            new int[]{CALL, ID, OPENPAREN, EXPRESSION, EXPR_LIST, CLOSEDPAREN});
    getAndTestSymbol(funcCall, CALL, "$");
  }
  
  @Test
  public void testAnonymousFunction() {
    String testCode = "func (a, b) {}";
    
    Node stmt = getTestStatement(testCode, ANONYMOUS_FUNC);
    testContainsSymbols(stmt, new int[]{FUNC, FUNCTION_BODY});
  }
}
