
package rowdy;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static rowdy.Rowdy.*;
import static rowdy.testUtils.TestUtils.*;

/**
 * Allows to test all forms of an expression tree.
 * @author Richard
 */
@RunWith(Parameterized.class)
public class RowdyBuilderExprTest {
  
  private final String testCode;
  
  public RowdyBuilderExprTest(String testCode) {
    this.testCode = testCode;
  }
  
  @Test
  public void testExpression() {
    testExpressionTree(testCode, ASSIGN_STMT);
  }
  
  public void testExpressionTree(String testCode, int parentId) {
    Node stmt = getTestStatement(testCode, parentId);
    Node expr = getAndTestSymbol(stmt, EXPRESSION, "expr");
    
    Node boolTerm = getAndTestSymbol(expr, BOOL_TERM, "bool-term");
    getAndTestSymbol(expr, BOOL_TERM_TAIL, "bool-term-tail");
    
    Node boolFactor = getAndTestSymbol(boolTerm, BOOL_FACTOR, "bool-factor");
    getAndTestSymbol(boolTerm, BOOL_FACTOR_TAIL, "bool-factor-tail");
    
    Node arithmExpr = getAndTestSymbol(boolFactor, ARITHM_EXPR, "arith-expr");
    getAndTestSymbol(boolFactor, RELATION_OPTION, "relation-option");
    
    Node term = getAndTestSymbol(arithmExpr, TERM, "term");
    getAndTestSymbol(arithmExpr, TERM_TAIL, "term-tail");
    
    getAndTestSymbol(term, FACTOR, "factor");
    getAndTestSymbol(term, FACTOR_TAIL, "factor-tail");
  }
  
  // Provide data
  @Parameterized.Parameters
  public static List<Object[]> data() {
    List<Object[]> list = new ArrayList<>();
    list.add(new Object[]{"e= a + b"});
    list.add(new Object[]{"e= 5 * "});
    list.add(new Object[]{"e= a ->a() + ->b()"});
    list.add(new Object[]{"e= a or b"});
    list.add(new Object[]{"e= ->a() or b"});
    list.add(new Object[]{"e= (a/ b) and b or/*-/+or or a"});
    return list;
  }
}
