
package rowdy;

import org.junit.Test;
import static rowdy.Rowdy.*;
import static rowdy.testUtils.TestUtils.*;
import static org.junit.Assert.*;

/**
 *
 * @author Richard
 */
public class RowdyBuilderArrayTest {
  
  @Test
  public void testArrayAccess() {
    String testCode = "e = get(arr, 5)";
    
    Node assignStmt = getTestStatement(testCode, ASSIGN_STMT);
    Node expr = getFromAndTestNotNull(assignStmt, EXPRESSION);
    testContainsSymbols(expr, 
            new int[]{GET,OPENPAREN,EXPRESSION,COMMA,EXPRESSION,CLOSEDPAREN});
    boolean e1 = getFromAndTestNotNull(expr, EXPRESSION, 0).getAll().isEmpty();
    boolean e2 = getFromAndTestNotNull(expr, EXPRESSION, 1).getAll().isEmpty();
    assertFalse(e1);
    assertFalse(e2);
  }
  
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
    testContainsSymbols(arrayBody, arraySymbols);
    Node arrayLinearBody = arrayBody.get(ARRAY_LINEAR_BODY);
    testContainsSymbols(arrayLinearBody, arraySymbols);
  }
  
  @Test
  public void testKeyValueArray() {
    String testCode = "a = array(a:0, b:1, c:3)";
    
    Node stmt = getTestStatement(testCode, ASSIGN_STMT);
    Node expr = getFromAndTestNotNull(stmt, EXPRESSION);
    Node arrayExpr = getFromAndTestNotNull(expr, ARRAY_EXPR); 
    Node arrayBody = getFromAndTestNotNull(arrayExpr, ARRAY_BODY);
    testContainsSymbols(arrayBody, 
            new int[]{COLON, EXPRESSION, ARRAY_KEY_VALUE_BODY_TAIL});
    
    Node arrayBodyTail = getFromAndTestNotNull(arrayBody, ARRAY_KEY_VALUE_BODY_TAIL);
    testContainsSymbols(arrayBodyTail, 
            new int[]{COMMA, EXPRESSION, ARRAY_KEY_VALUE_BODY});
  }
}
