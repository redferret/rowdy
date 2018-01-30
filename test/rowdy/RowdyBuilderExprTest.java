
package rowdy;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static rowdy.Rowdy.*;
import static rowdy.testUtils.TestUtils.*;
import static org.junit.Assert.*;

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
    Node boolTermTail = getAndTestSymbol(expr, BOOL_TERM_TAIL, "bool-term-tail");
    
    testNodeLeftChild(boolTermTail, 
            new int[][]{{OR, BOOL_TERM, BOOL_TERM_TAIL}});
    
    Node boolFactor = getAndTestSymbol(boolTerm, BOOL_FACTOR, "bool-factor");
    Node boolFactorTail = getAndTestSymbol(boolTerm, BOOL_FACTOR_TAIL, "bool-factor-tail");
    
    testNodeLeftChild(boolFactorTail, 
            new int[][]{{AND, BOOL_FACTOR, BOOL_FACTOR_TAIL}});
    
    Node arithmExpr = getAndTestSymbol(boolFactor, ARITHM_EXPR, "arith-expr");
    Node relationOpt = getAndTestSymbol(boolFactor, RELATION_OPTION, "relation-option");
    
    testNodeLeftChild(relationOpt, 
            new int[][]{{LESS, ARITHM_EXPR, RELATION_OPTION}, 
                        {GREATER, ARITHM_EXPR, RELATION_OPTION},
                        {EQUAL, ARITHM_EXPR, RELATION_OPTION}, 
                        {LESSEQUAL, ARITHM_EXPR, RELATION_OPTION}, 
                        {GREATEREQUAL, ARITHM_EXPR, RELATION_OPTION}, 
                        {NOTEQUAL, ARITHM_EXPR, RELATION_OPTION}});
    
    Node term = getAndTestSymbol(arithmExpr, TERM, "term");
    Node termTail = getAndTestSymbol(arithmExpr, TERM_TAIL, "term-tail");
    
    testNodeLeftChild(termTail, 
            new int[][]{{PLUS, TERM, TERM_TAIL}, {MINUS, TERM, TERM_TAIL}});
    
    Node factor = getAndTestSymbol(term, FACTOR, "factor");
    Node factorTail = getAndTestSymbol(term, FACTOR_TAIL, "factor-tail");
    
    testNodeLeftChild(factor, 
            new int[][]{{ATOMIC}, {MINUS, FACTOR}, 
                        {OPENPAREN, EXPRESSION, CLOSEDPAREN}});
    testNodeLeftChild(factorTail, 
            new int[][]{{MULTIPLY, FACTOR, FACTOR_TAIL}, 
                        {DIVIDE, FACTOR, FACTOR_TAIL}, 
                        {MOD, FACTOR, FACTOR_TAIL}, 
                        {POW, FACTOR, FACTOR_TAIL}});
  }
  
  
  public void testNodeLeftChild(Node testNode, final int cases[][]) {
    if (testNode.hasChildren()){
      boolean containsCase = false;
      for (int[] testCase : cases){
        for (int id : testCase) {
          Node node = testNode.get(id, false);
          if (node != null && node.getLeftMostChild() != null) {
            containsCase = true;
            break;
          }
        }
        if (containsCase) {
          break;
        }
      }
      assertTrue(containsCase);
    }
  }
  
  // Provide data
  @Parameterized.Parameters
  public static List<Object[]> data() {
    List<Object[]> list = new ArrayList<>();
    list.add(new Object[]{"e= a + b"});
    list.add(new Object[]{"e= 5 * 6"});
    list.add(new Object[]{"e= a *->a() + ->b()"});
    list.add(new Object[]{"e= a or b"});
    list.add(new Object[]{"e= a < b"});
    list.add(new Object[]{"e= a > b"});
    list.add(new Object[]{"e= a <= b"});
    list.add(new Object[]{"e= a >= b"});
    list.add(new Object[]{"e= a == b"});
    list.add(new Object[]{"e= a != ((a or b and c) != false)"});
    list.add(new Object[]{"e= a or c and b"});
    list.add(new Object[]{"e= ->a() or b"});
    list.add(new Object[]{"e= (a / (b))"});
    return list;
  }
}
