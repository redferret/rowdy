
package rowdy.nodes;

import growdy.Node;
import growdy.Symbol;
import growdy.NodeFactory;
import rowdy.BaseRowdyNode;
import rowdy.nodes.expression.*;
import rowdy.nodes.statement.*;
import static rowdy.lang.RowdyGrammarConstants.*;

/**
 * Directs GRowdy what types of nodes to build based on the given symbol
 * @author Richard
 */
public class RowdyNodeFactory implements NodeFactory {
  
  @Override
  public BaseRowdyNode getNode(Symbol symbol, int line) {
    switch(symbol.id()) {
      case EXPRESSION:
        Expression expr = new Expression(symbol, line);
        expr.setAsNonCompressable();
        return expr;
      case BOOL_TERM_TAIL:
        return new BoolOr(symbol, line);
      case BOOL_FACTOR_TAIL:
        return new BoolAnd(symbol, line);
      case RELATION_OPTION:
      case TERM:
      case TERM_TAIL:
      case FACTOR:
      case FACTOR_TAIL:
      case PAREN_EXPR:
      case ARITHM_EXPR:
      case BOOL_EXPR:
      case BOOL_TERM:
      case BOOL_FACTOR:
      case EXPRESSIONS:
        return new RowdyNode(symbol, line);
      case TERM_PLUS:
        return new TermPlus(symbol, line);
      case TERM_MINUS:
        return new TermMinus(symbol, line);
      case ARRAY_EXPR:
        return new ArrayExpression(symbol, line);
      case ROUND_EXPR:
        return new RoundExpr(symbol, line);
      case ISSET_EXPR:
        return new IssetExpr(symbol, line);
      case ANONYMOUS_FUNC:
        return new AnonymousFunc(symbol, line);
      case CONCAT_EXPR:
        return new ConcatExpr(symbol, line);
      case FACTOR_TAIL_DIV:
        return new FactorDiv(symbol, line);
      case FACTOR_TAIL_MUL:
        return new FactorMul(symbol, line);
      case FACTOR_TAIL_MOD:
        return new FactorMod(symbol, line);
      case FACTOR_TAIL_POW:
        return new FactorPow(symbol, line);
      case ATOMIC:
        Atomic atomic = new Atomic(symbol, line);
        atomic.setAsNonCompressable();
        return atomic;
      case ASSIGN_STMT:
        AssignStatement asgnstmt = new AssignStatement(symbol, line);
        asgnstmt.setAsNonCompressable();
        return asgnstmt;
      case IF_STMT:
        IfStatement ifstmt = new IfStatement(symbol, line);
        ifstmt.setAsNonCompressable();
        return ifstmt;
      case BREAK_STMT:
        BreakStatement bkstmt = new BreakStatement(symbol, line);
        bkstmt.setAsNonCompressable();
        return bkstmt;
      case READ_STMT:
        ReadStatement rdstmt = new ReadStatement(symbol, line);
        rdstmt.setAsNonCompressable();
        return rdstmt;
      case RETURN_STMT:
        ReturnStatement retstmt = new ReturnStatement(symbol, line);
        retstmt.setAsNonCompressable();
        return retstmt;
      case PRINT_STMT:
        PrintStatement pntstmt = new PrintStatement(symbol, line);
        pntstmt.setAsNonCompressable();
        return pntstmt;
      case LOOP_STMT:
        LoopStatement lpstmt = new LoopStatement(symbol, line);
        lpstmt.setAsNonCompressable();
        return lpstmt;
      default:
        RowdyNode atomicType = new RowdyNode(symbol, line);
        atomicType.setAsNonCompressable();
        return atomicType;
    }
  }
  
}
