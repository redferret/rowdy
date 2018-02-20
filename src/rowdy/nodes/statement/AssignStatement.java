
package rowdy.nodes.statement;

import growdy.Symbol;
import growdy.Terminal;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.CONST;
import static rowdy.lang.RowdyGrammarConstants.CONST_OPT;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;
import static rowdy.lang.RowdyGrammarConstants.ID;
import rowdy.nodes.RowdyNode;
import rowdy.nodes.expression.Expression;

/**
 *
 * @author Richard
 */
public class AssignStatement extends RowdyNode {
  
  public AssignStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    Terminal idTerminal = (Terminal) get(ID).symbol();
    Expression assignExpr = (Expression) get(EXPRESSION);
    Value rightValue = assignExpr.execute();
    if (get(CONST_OPT).get(CONST, false) != null) {
      rightValue.setAsConstant(true);
    }
    instance.allocate(idTerminal, rightValue);
    return null;
  }
  
}
