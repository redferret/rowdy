
package rowdy.nodes.statement;

import growdy.Node;
import growdy.Symbol;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import rowdy.nodes.expression.Expression;
import static rowdy.lang.RowdyGrammarConstants.ELSE_PART;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;
import static rowdy.lang.RowdyGrammarConstants.STMT_BLOCK;
import static rowdy.lang.RowdyGrammarConstants.STMT_LIST;

/**
 *
 * @author Richard
 */
public class IfStatement extends RowdyNode {
  
  public IfStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }

  @Override
  public Value execute(Value seqControlWrapper) throws ConstantReassignmentException {
    Node seqControl = (Node) seqControlWrapper.getValue();
    Expression ifExpr = (Expression) get(EXPRESSION);
    Value ifExprValue = ifExpr.execute();
    if (ifExprValue.valueToBoolean()) {
      Node ifStmtList = get(STMT_BLOCK).get(STMT_LIST);
      instance.executeStmt(ifStmtList, seqControl);
    } else {
      instance.executeStmt(get(ELSE_PART), seqControl);
    }
    return null;
  }
}
