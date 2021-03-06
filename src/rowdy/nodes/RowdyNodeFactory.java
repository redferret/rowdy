
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
      case ID:
      case CONSTANT:
      case CAST_STR:
      case CAST_INT:
      case CAST_BINT:
      case CAST_SHRT:
      case CAST_BYT:
      case CAST_LNG:
      case CAST_BOL:
      case CAST_DBL:
      case INCREMENT:
      case DECREMENT:
      case SUPER:
      case CONST:
      case GLOBAL:
      case THIS:
      case PUBLIC:
      case PRIVATE:
      case BREAK:
      case THIS_:
      case FUNCTION_BODY:
      case ARRAY_ACCESS:
      case FUNC_PARAMS:
      case REF_ACCESS:
      case INHERIT_OPT:
      case DYNAMIC:
        RowdyNode terminal = new RowdyNode(symbol, line);
        terminal.setAsNonCompressable();
        terminal.setAsCriticalTerminal();
        return terminal;
      case NULL_DEFAULT:
        NullDefault nullDefault = new NullDefault(symbol, line);
        nullDefault.setAsNonCompressable();
        return nullDefault;
      case NEW_OBJ:
        NewObject newObject = new NewObject(symbol, line);
        newObject.setAsNonCompressable();
        return newObject;
      // EXPRESSION shouldn't be both non compressable and reducable
      case EXPRESSION:
        Expression expr = new Expression(symbol, line);
        expr.setAsNonCompressable();
        expr.reduce();
        return expr;
      case BOOL_TERM_TAIL:
        BoolOr or = new BoolOr(symbol, line);
        or.setAsNonCompressable();
        return or;
      case BOOL_FACTOR_TAIL:
        BoolAnd and = new BoolAnd(symbol, line);
        and.setAsNonCompressable();
        return and;
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
      case COMPOUND_ASSIGN:
      case ATOMIC:
      case STATEMENT:
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
       case ARITHM_NOTEQUAL:
        RelNotEqual noteq = new RelNotEqual(symbol, line);
        noteq.setAsNonCompressable();
        return noteq;
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
        arrayExpr.setAsCriticalTerminal();
        return arrayExpr;
      case MAP_EXPR:
        MapExpression mapExpr = new MapExpression(symbol, line);
        mapExpr.setAsNonCompressable();
        mapExpr.setAsCriticalTerminal();
        return mapExpr;
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
      case ATOMIC_ID:
        AtomicId atomicId = new AtomicId(symbol, line);
        atomicId.setAsNonCompressable();
        return atomicId;
      case ATOMIC_FUNC_CALL:
        AtomicFuncCall atomicFuncCall = new AtomicFuncCall(symbol, line);
        atomicFuncCall.setAsNonCompressable();
        return atomicFuncCall;
      
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
        retstmt.setAsCriticalTerminal();
        return retstmt;
      case PRINT_STMT:
        PrintStatement pntstmt = new PrintStatement(symbol, line);
        pntstmt.setAsNonCompressable();
        return pntstmt;
      case LOOP_STMT:
        LoopStatement lpstmt = new LoopStatement(symbol, line);
        lpstmt.setAsNonCompressable();
        return lpstmt;
      case THROW_STMT:
        ThrowStatement throwstmt = new ThrowStatement(symbol, line);
        throwstmt.setAsNonCompressable();
        return throwstmt;
      case WHILE_LOOP:
        WhileLoop whileLoop = new WhileLoop(symbol, line);
        whileLoop.setAsNonCompressable();
        return whileLoop;
      // STMT_BLOCK shouldn't be both non compressable and reducable
      case STMT_BLOCK:
        RowdyNode stmtBlock = new RowdyNode(symbol, line);
        stmtBlock.setAsNonCompressable();
        stmtBlock.reduce();
        return stmtBlock;
      default:
        RowdyNode defaultNode = new RowdyNode(symbol, line);
        defaultNode.setAsNonCompressable();
        return defaultNode;
    }
  }
  
}
