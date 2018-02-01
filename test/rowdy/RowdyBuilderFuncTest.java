
package rowdy;

import org.junit.Test;
import static rowdy.Rowdy.*;
import static rowdy.testUtils.TestUtils.*;
import static org.junit.Assert.*;
/**
 *
 * @author Richard DeSilvey
 */
public class RowdyBuilderFuncTest {
  
  @Test
  public void testFunction() {
    String testCode = "func name(a, b){}";
    
    Node function = getTestProgram(testCode, FUNCTION);
    assertTrue("Function shouldn't be empty", function.hasChildren());
    
    getAndTestSymbol(function, FUNC, "func");
    getFromAndTestNotNull(function, ID);
    Node functionBody = getAndTestSymbol(function, FUNCTION_BODY, "func-body");
    
    testContainsSymbols(functionBody, 
            new int[]{OPENPAREN, PARAMETERS, CLOSEDPAREN, STMT_BLOCK});
    Node parameters = getAndTestSymbol(functionBody, PARAMETERS, "params");
    testContainsSymbols(parameters, new int[]{ID, PARAMS_TAIL});
    Node paramTail = getAndTestSymbol(parameters, PARAMS_TAIL, "params-tail");
    testContainsSymbols(paramTail, new int[]{COMMA, ID, PARAMS_TAIL});
  }
  
  @Test
  public void testFunctionCall() {
    String testCode = "->fun(1, 2, A)";
    
    Node funcCall = getTestStatement(testCode, FUNC_CALL);
    testContainsSymbols(funcCall, 
            new int[]{CALL, ID, OPENPAREN, EXPRESSION, EXPR_LIST, CLOSEDPAREN});
    getAndTestSymbol(funcCall, CALL, "->");
    testExpressionList(funcCall);
  }
  
  @Test
  public void testAnonymousFunction() {
    String testCode = "f = func (a, b) {}";
    
    Node stmt = getTestStatement(testCode, ASSIGN_STMT);
    Node expr = getFromAndTestNotNull(stmt, EXPRESSION);
    Node anonFunc = getFromAndTestNotNull(expr, ANONYMOUS_FUNC);
    testContainsSymbols(expr, new int[]{FUNC, ANONYMOUS_FUNC});
    testContainsSymbols(anonFunc, new int[]{FUNCTION_BODY});
  }
}
