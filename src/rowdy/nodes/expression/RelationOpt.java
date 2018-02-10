
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.ARITHM_EQUAL;
import static rowdy.lang.RowdyGrammarConstants.ARITHM_EXPR;
import static rowdy.lang.RowdyGrammarConstants.ARITHM_GREATER;
import static rowdy.lang.RowdyGrammarConstants.ARITHM_GREATEREQUAL;
import static rowdy.lang.RowdyGrammarConstants.ARITHM_LESS;
import static rowdy.lang.RowdyGrammarConstants.ARITHM_LESSEQUAL;
import static rowdy.lang.RowdyGrammarConstants.ARITHM_NOTEQUAL;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class RelationOpt extends RowdyNode {
  
  public RelationOpt(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    RowdyNode child = (RowdyNode) getLeftMost();
    if (child == null) {
      return runner.fetch(leftValue, this);
    }
    ArithmExpr arithmExpr = (ArithmExpr) child.get(ARITHM_EXPR);
    leftValue = runner.fetch(leftValue, this);
    Value rightValue = arithmExpr.execute(leftValue);
    double left, right;
    if (leftValue.getValue() instanceof Boolean) {
      left = leftValue.valueToBoolean() ? 1 : 0;
    } else {
      left = leftValue.valueToDouble();
    }
    if (rightValue.getValue() instanceof Boolean) {
      right = rightValue.valueToBoolean() ? 1 : 0;
    } else {
      right = rightValue.valueToDouble();
    }
    switch(child.symbol().id()) {
      case ARITHM_LESS:
        return new Value(left < right);
      case ARITHM_LESSEQUAL:
        return new Value(left <= right);
      case ARITHM_EQUAL:
        return new Value(left == right);
      case ARITHM_GREATEREQUAL:
        return new Value(left >= right);
      case ARITHM_GREATER:
        return new Value(left > right);
      case ARITHM_NOTEQUAL:
        return new Value(left != right);
      default:
        return leftValue;
    }
  }
}
