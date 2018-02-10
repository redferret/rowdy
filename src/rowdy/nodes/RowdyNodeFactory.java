
package rowdy.nodes;

import growdy.Node;
import growdy.Symbol;
import growdy.NodeFactory;
import rowdy.RowdyRunner;
import rowdy.nodes.expression.*;
import static rowdy.lang.RowdyGrammarConstants.*;

/**
 *
 * @author Richard
 */
public class RowdyNodeFactory implements NodeFactory {

  private final RowdyRunner runner;

  public RowdyNodeFactory(RowdyRunner runner) {
    this.runner = runner;
  }
  
  @Override
  public Node getNode(Symbol symbol, int line) {
    switch(symbol.id()) {
      case EXPRESSION:
        return new Expression(symbol, line, runner);
      case BOOL_TERM:
        return new BoolTerm(symbol, line, runner);
      case BOOL_TERM_TAIL:
        return new BoolTermTail(symbol, line, runner);
      case BOOL_FACTOR:
        return new BoolFactor(symbol, line, runner);
      case BOOL_FACTOR_TAIL:
        return new BoolFactorTail(symbol, line, runner);
      case ARITHM_EXPR:
        return new ArithmExpr(symbol, line, runner);
      case RELATION_OPTION:
        return new RelationOpt(symbol, line, runner);
      case TERM:
        return new Term(symbol, line, runner);
      case TERM_TAIL:
        return new TermTail(symbol, line, runner);
      case FACTOR:
        return new Factor(symbol, line, runner);
      case FACTOR_TAIL:
        return new FactorTail(symbol, line, runner);
      case ATOMIC:
        return new Atomic(symbol, line, runner);
      default:
        return new RowdyNode(symbol, line, runner);
    }
  }
  
}
