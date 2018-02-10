
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class ParenExpr extends RowdyNode {

  public ParenExpr(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }
  @Override
  public Value execute(RowdyNode cur, Value leftValue) throws ConstantReassignmentException {
    Expression expr = (Expression) cur.get(EXPRESSION);
    return expr.execute(leftValue);
  }
}
