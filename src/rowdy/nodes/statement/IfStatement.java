
package rowdy.nodes.statement;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.ELSE_IF_PART;
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
  public Object execute(Object seqControl) {
    BaseNode ifExpr = getLeftMost();
    Value ifExprValue = (Value) ifExpr.execute();
    try {
      if ((boolean) ifExprValue.getValue()) {
        BaseNode ifStmtList = get(STMT_LIST);
        instance.executeStmt(ifStmtList, (BaseNode) seqControl);
      } else {
        instance.executeStmt(get(ELSE_IF_PART), (BaseNode) seqControl);
      }
    } catch (ConstantReassignmentException ex) {
      ex.printStackTrace();
    }
    return null;
  }
}
