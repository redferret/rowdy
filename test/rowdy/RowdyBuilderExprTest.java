
package rowdy;

import growdy.Node;
import static growdy.testUtils.TestUtils.getAndTestSymbol;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;
import static rowdy.testlang.lang.RowdyGrammarConstants.*;
import static rowdy.testutils.TestUtils.getTestStatement;

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
    testExpressionTree(testCode, EXPRESSION);
  }
  
  public void testExpressionTree(String testCode, int parentId) {
    Node stmt = getTestStatement(testCode, parentId);
    Node expr = getAndTestSymbol(stmt, EXPRESSIONS, "expressions").get(BOOL_EXPR);
    
    Node boolTerm = getAndTestSymbol(expr, BOOL_TERM, "bool-term");
    Node boolTermTail = getAndTestSymbol(expr, BOOL_TERM_TAIL, "bool-term-tail");
    
    testNodeLeftChild(boolTermTail, 
            new int[][]{{OR, BOOL_TERM, BOOL_TERM_TAIL}});
    
    Node boolFactor = getAndTestSymbol(boolTerm, BOOL_FACTOR, "bool-factor");
    Node boolFactorTail = getAndTestSymbol(boolTerm, BOOL_FACTOR_TAIL, "bool-factor-tail");
    
    testNodeLeftChild(boolFactorTail, 
            new int[][]{{AND, BOOL_FACTOR, BOOL_FACTOR_TAIL}});
    
    Node arithmExpr = getAndTestSymbol(boolFactor, ARITHM_EXPR, "arithm-expr");
    Node relationOpt = getAndTestSymbol(boolFactor, RELATION_OPTION, "relation-option");
    
    Node relationOpts = relationOpt.getLeftMost();
    testNodeLeftChild(relationOpts, 
            new int[][]{{LESS, ARITHM_EXPR, RELATION_OPTION}, 
                        {GREATER, ARITHM_EXPR, RELATION_OPTION},
                        {EQUAL, ARITHM_EXPR, RELATION_OPTION}, 
                        {LESSEQUAL, ARITHM_EXPR, RELATION_OPTION}, 
                        {GREATEREQUAL, ARITHM_EXPR, RELATION_OPTION}, 
                        {NOTEQUAL, ARITHM_EXPR, RELATION_OPTION}});
    
    Node term = getAndTestSymbol(arithmExpr, TERM, "term");
    Node termTail = getAndTestSymbol(arithmExpr, TERM_TAIL, "term-tail");
    
    Node termPlusMinus = termTail.getLeftMost();
    testNodeLeftChild(termPlusMinus, 
            new int[][]{{PLUS, TERM, TERM_TAIL}, {MINUS, TERM, TERM_TAIL}});
    
    Node factor = getAndTestSymbol(term, FACTOR, "factor");
    Node factorTail = getAndTestSymbol(term, FACTOR_TAIL, "factor-tail");
    
    testNodeLeftChild(factor, 
            new int[][]{{PAREN_EXPR},
                        {ATOMIC}, {MINUS, FACTOR}, 
                        {OPENPAREN, EXPRESSION, CLOSEDPAREN}});
    Node factorLeft = factorTail.getLeftMost();
    testNodeLeftChild(factorLeft, 
            new int[][]{{MULTIPLY, FACTOR, FACTOR_TAIL}, 
                        {DIVIDE, FACTOR, FACTOR_TAIL}, 
                        {MOD, FACTOR, FACTOR_TAIL}, 
                        {POW, FACTOR, FACTOR_TAIL}});
    }
  
  
  public void testNodeLeftChild(Node testNode, final int cases[][]) {
    if (testNode != null && testNode.hasSymbols()){
      boolean containsCase = false;
      for (int[] testCase : cases){
        for (int id : testCase) {
          Node node = testNode.get(id, false);
          if (node != null && node.getLeftMost() != null) {
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
    list.add(new Object[]{"a + b"});
    list.add(new Object[]{"5 * 6"});
    list.add(new Object[]{"5 / 6"});
    list.add(new Object[]{"5 % 6"});
    list.add(new Object[]{"5 ^ -6"});
    list.add(new Object[]{"a *$a() + $b()"});
    list.add(new Object[]{"a or b"});
    list.add(new Object[]{"a < b"});
    list.add(new Object[]{"a > b"});
    list.add(new Object[]{"a <= b"});
    list.add(new Object[]{"a >= b"});
    list.add(new Object[]{"a == b"});
    list.add(new Object[]{"a != ((a or b and c) != false)"});
    list.add(new Object[]{"a or c and b"});
    list.add(new Object[]{"$a() or b"});
    list.add(new Object[]{"(a / (b))"});
    return list;
  }
}
