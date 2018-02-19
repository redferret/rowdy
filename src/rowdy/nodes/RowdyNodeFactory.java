
package rowdy.nodes;

import growdy.Node;
import growdy.Symbol;
import growdy.NodeFactory;
import rowdy.nodes.expression.*;
import rowdy.nodes.statement.*;
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
      case EXPRESSIONS:
        return new Expressions(symbol, line);
      case ASSIGN_STMT:
        return new AssignStatement(symbol, line);
      case IF_STMT:
        return new IfStatement(symbol, line);
      case BREAK_STMT:
        return new BreakStatement(symbol, line);
      case READ_STMT:
        return new ReadStatement(symbol, line);
      case FUNC_CALL:
        return new FunctionCall(symbol, line);
      case RETURN_STMT:
        return new ReturnStatement(symbol, line);
      case PRINT_STMT:
        return new PrintStatement(symbol, line);
      case LOOP_STMT:
        return new LoopStatement(symbol, line);
      default:
        return new RowdyNode(symbol, line);
    }
  }
  
}
