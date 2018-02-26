
package rowdy.nodes;

import growdy.Symbol;
import growdy.NodeFactory;
import rowdy.BaseNode;
import rowdy.nodes.expression.*;
import rowdy.nodes.statement.*;
import static rowdy.lang.RowdyGrammarConstants.*;

/**
 * Directs GRowdy what types of nodes to build based on the given symbol
 * @author Richard
 */
public class RowdyNodeFactory implements NodeFactory {
  
  @Override
  public BaseNode getNode(Symbol symbol, int line) {
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
      case ARITHM_GREATER:
        RelGreater greater = new RelGreater(symbol, line);
        greater.setAsNonCompressable();
        return greater;
      case ARITHM_GREATEREQUAL:
        RelGreaterEqual greatereq = new RelGreaterEqual(symbol, line);
        greatereq.setAsNonCompressable();
        return greatereq;
      case ARITHM_LESS:
        RelLess less = new RelLess(symbol, line);
        less.setAsNonCompressable();
        return less;
      case ARITHM_LESSEQUAL:
        RelLessEqual lesseq = new RelLessEqual(symbol, line);
        lesseq.setAsNonCompressable();
        return lesseq;
      case ARITHM_EQUAL:
        RelEqual eq = new RelEqual(symbol, line);
        eq.setAsNonCompressable();
        return eq;
      case TERM_PLUS:
        TermPlus termPlus = new TermPlus(symbol, line);
        termPlus.setAsNonCompressable();
        return termPlus;
      case TERM_MINUS:
        TermMinus termMinus = new TermMinus(symbol, line);
        termMinus.setAsNonCompressable();
        return termMinus;
      case ARRAY_EXPR:
        ArrayExpression arrayExpr = new ArrayExpression(symbol, line);
        arrayExpr.setAsNonCompressable();
        return arrayExpr;
      case ROUND_EXPR:
        RoundExpr roundExpr = new RoundExpr(symbol, line);
        roundExpr.setAsNonCompressable();
        return roundExpr;
      case ISSET_EXPR:
        IssetExpr issetExpr = new IssetExpr(symbol, line);
        issetExpr.setAsNonCompressable();
        return issetExpr;
      case ANONYMOUS_FUNC:
        AnonymousFunc afunc = new AnonymousFunc(symbol, line);
        afunc.setAsNonCompressable();
        return afunc;
      case CONCAT_EXPR:
        ConcatExpr concat = new ConcatExpr(symbol, line);
        concat.setAsNonCompressable();
        return concat;
      case FACTOR_TAIL_DIV:
        FactorDiv div = new FactorDiv(symbol, line);
        div.setAsNonCompressable();
        return div;
      case FACTOR_TAIL_MUL:
        FactorMul mul = new FactorMul(symbol, line);
        mul.setAsNonCompressable();
        return mul;
      case FACTOR_TAIL_MOD:
        FactorMod mod = new FactorMod(symbol, line);
        mod.setAsNonCompressable();
        return mod;
      case FACTOR_TAIL_POW:
        FactorPow pow = new FactorPow(symbol, line);
        pow.setAsNonCompressable();
        return pow;
      case FACTOR_MINUS:
        FactorMinus minus = new FactorMinus(symbol, line);
        minus.setAsNonCompressable();
        return minus;
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
