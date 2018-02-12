
package rowdy;

import growdy.Node;

import org.junit.Test;
import static org.junit.Assert.*;
import static rowdy.testlang.lang.RowdyGrammarConstants.*;
import static growdy.testUtils.TestUtils.*;
import static rowdy.testutils.TestUtils.getTestStatement;
/**
 *
 * @author Richard
 */
public class RowdyBuilderArrayTest {
  
  @Test
  public void testEmptyLinearArray() {
    String testCode = "a = array()";
    Node stmt = getTestStatement(testCode, ASSIGN_STMT);
    Node expr = getFromAndTestNotNull(stmt, EXPRESSION);
    Node arrayExpr = getFromAndTestNotNull(expr, ARRAY_EXPR);
    
    getAndTestSymbol(arrayExpr, ARRAY, "array");
    testContainsSymbols(arrayExpr, 
            new int[]{ARRAY, OPENPAREN, EXPRESSION, ARRAY_BODY, CLOSEDPAREN});
  }
  
  @Test
  public void testLinearArray() {
    String testCode = "a = array(a, b, 7)";
    
    Node stmt = getTestStatement(testCode, ASSIGN_STMT);
    Node expr = getFromAndTestNotNull(stmt, EXPRESSION);
    Node arrayExpr = getFromAndTestNotNull(expr, ARRAY_EXPR);
    
    final int[] arraySymbols = new int[]{COMMA, EXPRESSION, ARRAY_LINEAR_BODY};
    Node arrayBody = getFromAndTestNotNull(arrayExpr, ARRAY_BODY);
    Node arrayLinearBody = arrayBody.get(ARRAY_LINEAR_BODY);
    testContainsSymbols(arrayLinearBody, arraySymbols);
    testContainsSymbols(arrayLinearBody, arraySymbols);
  }
  
  @Test
  public void testKeyValueArray() {
    String testCode = "a = array(a:0, b:1, c:3)";
    
    Node stmt = getTestStatement(testCode, ASSIGN_STMT);
    Node expr = getFromAndTestNotNull(stmt, EXPRESSION);
    Node arrayExpr = getFromAndTestNotNull(expr, ARRAY_EXPR); 
    Node arrayBody = getFromAndTestNotNull(arrayExpr, ARRAY_BODY);
    Node keyValueBody = getFromAndTestNotNull(arrayBody,ARRAY_KEY_VALUE_BODY );
    testContainsSymbols(keyValueBody, 
            new int[]{COLON, EXPRESSION, ARRAY_KEY_VALUE_BODY_TAIL});
    
    Node arrayBodyTail = getFromAndTestNotNull(keyValueBody, ARRAY_KEY_VALUE_BODY_TAIL);
    testContainsSymbols(arrayBodyTail, 
            new int[]{COMMA, EXPRESSION, ARRAY_KEY_VALUE_BODY});
  }
}
