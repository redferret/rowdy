
package rowdy.nodes.statement;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.ELSE_IF_PART;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;
import static rowdy.lang.RowdyGrammarConstants.STMT_BLOCK;
import static rowdy.lang.RowdyGrammarConstants.STMT_LIST;

/**
 *
 * @author Richard
 */
public class IfStatement extends BaseNode {
  
  public IfStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }

  @Override
  public Value execute(Value seqControlWrapper) {
    BaseNode seqControl = (BaseNode) seqControlWrapper.getValue();
    BaseNode ifExpr = get(EXPRESSION);
    Value ifExprValue = ifExpr.execute();
    try {
      if (ifExprValue.valueToBoolean()) {
        BaseNode ifStmtList = get(STMT_BLOCK).get(STMT_LIST);
        instance.executeStmt(ifStmtList, seqControl);
      } else {
        instance.executeStmt(get(ELSE_IF_PART), seqControl);
      }
    } catch (ConstantReassignmentException ex) {
      ex.printStackTrace();
    }
    return null;
  }
}
