
package rowdy.nodes.statement;

import growdy.Symbol;
import growdy.Terminal;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import rowdy.nodes.expression.Expression;
import rowdy.Function;
import rowdy.SymbolTable;
import static rowdy.lang.RowdyGrammarConstants.CONST_OPT;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;
import static rowdy.lang.RowdyGrammarConstants.GLOBAL_DEF;
import static rowdy.lang.RowdyGrammarConstants.ID;
import static rowdy.lang.RowdyGrammarConstants.ID_MODIFIER;
import static rowdy.lang.RowdyGrammarConstants.THIS_REF;

/**
 *
 * @author Richard
 */
public class AssignStatement extends RowdyNode {
  
  public AssignStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    Terminal idTerminal = (Terminal) get(ID).symbol();
    Expression assignExpr = (Expression) get(EXPRESSION);
    Value rightValue = assignExpr.execute();
    
    if (rightValue.isConstant()) {
      rightValue.setAsConstant(false);
    }
    
    RowdyNode globalMod = (RowdyNode) get(GLOBAL_DEF);
    RowdyNode idModifier = (RowdyNode) get(ID_MODIFIER);
    if (idModifier.getLeftMost() != null) {
      switch (idModifier.getLeftMost().symbol().id()) {
        case CONST_OPT:
          rightValue.setAsConstant(true);
          break;
        case THIS_REF:
          Function curFunction = instance.callStack.peek();
          SymbolTable table;
          if (curFunction.isIsMemberFunction()) {
            table = curFunction.getParent().getSymbolTable();
          } else {
            table = curFunction.getSymbolTable();
          }
          table.allocate(idTerminal, rightValue, getLine());
          return null;
      }
    }
    if (globalMod.hasSymbols()) {
      instance.setAsGlobal(idTerminal, rightValue);
    } else {
      instance.allocate(idTerminal, rightValue, getLine());
    }
    return null;
  }
  
}
