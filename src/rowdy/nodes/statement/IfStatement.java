
package rowdy.nodes.statement;

import growdy.Symbol;
import rowdy.BaseRowdyNode;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.expression.Expression;
import static rowdy.lang.RowdyGrammarConstants.ELSE_PART;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;
import static rowdy.lang.RowdyGrammarConstants.STMT_BLOCK;
import static rowdy.lang.RowdyGrammarConstants.STMT_LIST;

/**
 *
 * @author Richard
 */
public class IfStatement extends BaseRowdyNode {
  
  public IfStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }

  @Override
  public Value execute(Value seqControlWrapper) {
    BaseRowdyNode seqControl = (BaseRowdyNode) seqControlWrapper.getValue();
    Expression ifExpr = (Expression) get(EXPRESSION);
    Value ifExprValue = ifExpr.execute();
    try {
      if (ifExprValue.valueToBoolean()) {
        BaseRowdyNode ifStmtList = get(STMT_BLOCK).get(STMT_LIST);
        instance.executeStmt(ifStmtList, seqControl);
      } else {
        instance.executeStmt(get(ELSE_PART), seqControl);
      }
    } catch (ConstantReassignmentException ex) {
    }
    return null;
  }
}
