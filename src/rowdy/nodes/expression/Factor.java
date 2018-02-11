
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;
import static rowdy.lang.RowdyGrammarConstants.FACTOR;
import static rowdy.lang.RowdyGrammarConstants.FACTOR_MINUS;
import static rowdy.lang.RowdyGrammarConstants.PAREN_EXPR;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class Factor extends RowdyNode {

  public Factor(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    RowdyNode child = (RowdyNode) getLeftMost();
    
    switch(child.symbol().id()) {
      case FACTOR_MINUS:
        Factor factor = (Factor) child.get(FACTOR);
        leftValue = runner.fetch(leftValue, child);
        double leftVal = 0;
        if (leftValue != null) {
          leftVal = leftValue.valueToDouble();
        }
        double rightVal = factor.execute(leftValue).valueToDouble();
        return new Value(leftVal - rightVal);
      case ATOMIC:
        return ((Atomic)child).execute(leftValue);
      case PAREN_EXPR:
        Expression expr = (Expression) child.get(EXPRESSION);
        return expr.execute(leftValue);
    }
    
    return child.execute(leftValue);
  }
}
