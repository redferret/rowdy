
package rowdy.nodes.expression;

import growdy.Node;
import growdy.Symbol;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import static rowdy.lang.RowdyGrammarConstants.*;


/**
 *
 * @author Richard
 */
public class Expressions extends RowdyNode {
  public Expressions(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    RowdyNode node = (RowdyNode) getLeftMost();
    switch (node.symbol().id()){
      case BOOL_EXPR:
        BoolTerm boolTerm = (BoolTerm) node.getLeftMost();
        BoolTermTail boolTermTail = (BoolTermTail) node.get(BOOL_TERM_TAIL);
        leftValue = boolTerm.execute();
        return boolTermTail.execute(leftValue);
      case ARRAY_EXPR:
        return ((ArrayExpression)node).execute();
      case ISSET_EXPR:
        Value idTerm = runner.getIdAsValue(node.get(ID));
        Value resultBoolean = new Value(runner.isset(idTerm), false);
        return resultBoolean;
      case CONCAT_EXPR:
        StringBuilder concatValue = new StringBuilder();
        Expression concatExpr = (Expression) node.getLeftMost().get(EXPRESSION);
        concatValue.append(concatExpr.execute(leftValue).valueToString());
        Node atomTailNode = node.getLeftMost().get(EXPR_LIST);
        while (atomTailNode.hasSymbols()) {
          concatExpr = (Expression) atomTailNode.get(EXPRESSION);
          concatValue.append(concatExpr.execute(leftValue).valueToString());
          atomTailNode = atomTailNode.get(EXPR_LIST);
        }
        return new Value(concatValue.toString(), false);
      case ANONYMOUS_FUNC:
        return new Value(node, false);
      case ROUND_EXPR:
        Value valueToRound = runner.fetch(runner.getIdAsValue(node.get(ID)), node);
        double roundedValue = valueToRound.valueToDouble();
        ArithmExpr arithmExpr = (ArithmExpr) node.get(ARITHM_EXPR);
        int precision = arithmExpr.execute().valueToDouble().intValue();
        double factor = 1;
        while (precision > 0) {
          factor *= 10;
          precision--;
        }
        roundedValue = (double) Math.round(roundedValue * factor) / factor;
        return new Value(roundedValue, false);
      default:
        return runner.fetch(leftValue, this);
    }
  }
}
