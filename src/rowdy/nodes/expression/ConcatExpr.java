
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseRowdyNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;
import static rowdy.lang.RowdyGrammarConstants.EXPR_LIST;

/**
 *
 * @author Richard
 */
public class ConcatExpr extends BaseRowdyNode {

  public ConcatExpr(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }

  @Override
  public Value execute(Value leftValue) {
    StringBuilder concatValue = new StringBuilder();
    BaseRowdyNode concatExpr = get(EXPRESSION);
    concatValue.append(concatExpr.execute(leftValue).valueToString());
    BaseRowdyNode atomTailNode = get(EXPR_LIST);
    while (atomTailNode.hasSymbols()) {
      concatExpr = (Expression) atomTailNode.get(EXPRESSION);
      concatValue.append(concatExpr.execute(leftValue).valueToString());
      atomTailNode = atomTailNode.get(EXPR_LIST);
    }
    return new Value(concatValue.toString(), false);
  }
  
}
