
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.RowdyInstance;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.RELATION_OPTION;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class BoolFactor extends RowdyNode {
  
  public BoolFactor(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    ArithmExpr arithmExpr = (ArithmExpr) getLeftMost();
    RelationOpt relationOpt = (RelationOpt) get(RELATION_OPTION);
    leftValue = arithmExpr.execute(leftValue);
    return relationOpt.execute(leftValue);
  }
}
