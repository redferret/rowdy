
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
public class Expression extends RowdyNode {

  public Expression(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    RowdyNode node = (RowdyNode) get(BOOL_EXPR, false);
    if (node == null) {
      return runner.fetch(leftValue, this);
    }
    switch (node.symbol().id()){
      case BOOL_EXPR:
        BoolTerm boolTerm = (BoolTerm) node.getLeftMost();
        BoolTermTail boolTermTail = (BoolTermTail) node.get(BOOL_TERM_TAIL);
        leftValue = boolTerm.execute();
        return boolTermTail.execute(leftValue);
      case ISSET_EXPR:
        Node issetExpr = node.get(ISSET_EXPR);
        Value idTerm = runner.getIdAsValue(issetExpr.get(ID));
        Value resultBoolean = new Value(runner.isset(idTerm));
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
        return new Value(concatValue.toString());
      case ANONYMOUS_FUNC:
        Node anonymousFunc = node.get(ANONYMOUS_FUNC);
        return new Value(anonymousFunc);
      case ROUND_EXPR:
        Node roundExpr = node.get(ROUND_EXPR);
        Value valueToRound = runner.fetch(runner.getIdAsValue(roundExpr.get(ID)), node);
        double roundedValue = valueToRound.valueToDouble();
        ArithmExpr arithmExpr = (ArithmExpr) roundExpr.get(ARITHM_EXPR);
        int precision = arithmExpr.execute().valueToDouble().intValue();
        double factor = 1;
        while (precision > 0) {
          factor *= 10;
          precision--;
        }
        roundedValue = (double) Math.round(roundedValue * factor) / factor;
        return new Value(roundedValue);
      default:
        return leftValue;
    }
  }
  
}
