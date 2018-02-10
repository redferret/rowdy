
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.RELATION_OPTION;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class BoolFactor extends RowdyNode {
  
  public BoolFactor(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }
  @Override
  public Value execute(RowdyNode cur, Value leftValue) throws ConstantReassignmentException {
    ArithmExpr arithmExpr = (ArithmExpr) cur.getLeftMost();
    RelationOpt relationOpt = (RelationOpt) cur.get(RELATION_OPTION);
    leftValue = arithmExpr.execute(leftValue);
    return relationOpt.execute(leftValue);
  }
}
