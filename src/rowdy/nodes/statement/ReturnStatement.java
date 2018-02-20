
package rowdy.nodes.statement;

import growdy.Node;
import growdy.Symbol;
import rowdy.Function;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;
import rowdy.nodes.RowdyNode;
import rowdy.nodes.expression.Expression;

/**
 *
 * @author Richard
 */
public class ReturnStatement extends RowdyNode {
  
  public ReturnStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value seqControlWrapper) throws ConstantReassignmentException {
    Node seqControl = (Node) seqControlWrapper.getValue();
    Function functionReturning = instance.callStack.peek();
    seqControl.setSeqActive(false);
    Expression returnExpr = (Expression) get(EXPRESSION);
    Value toSet = returnExpr.execute();
    functionReturning.setReturnValue(toSet);
    return null;
  }
}
