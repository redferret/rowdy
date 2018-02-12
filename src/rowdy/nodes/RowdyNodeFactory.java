
package rowdy.nodes;

import growdy.Node;
import growdy.Symbol;
import growdy.NodeFactory;
import rowdy.nodes.expression.*;
import static rowdy.lang.RowdyGrammarConstants.*;

/**
 *
 * @author Richard
 */
public class RowdyNodeFactory implements NodeFactory {
  
  @Override
  public Node getNode(Symbol symbol, int line) {
    switch(symbol.id()) {
      case EXPRESSION:
        return new Expression(symbol, line);
      case BOOL_TERM:
        return new BoolTerm(symbol, line);
      case BOOL_TERM_TAIL:
        return new BoolTermTail(symbol, line);
      case BOOL_FACTOR:
        return new BoolFactor(symbol, line);
      case BOOL_FACTOR_TAIL:
        return new BoolFactorTail(symbol, line);
      case ARITHM_EXPR:
        return new ArithmExpr(symbol, line);
      case RELATION_OPTION:
        return new RelationOpt(symbol, line);
      case TERM:
        return new Term(symbol, line);
      case TERM_TAIL:
        return new TermTail(symbol, line);
      case FACTOR:
        return new Factor(symbol, line);
      case FACTOR_TAIL:
        return new FactorTail(symbol, line);
      case ATOMIC:
        return new Atomic(symbol, line);
      case ARRAY_EXPR:
        return new ArrayExpression(symbol, line);
      case ROUND_EXPR:
        return new RoundExpr(symbol, line);
      default:
        return new RowdyNode(symbol, line);
    }
  }
  
}
