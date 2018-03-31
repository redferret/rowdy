
package rowdy.nodes.statement;

import growdy.Symbol;
import growdy.Terminal;
import java.util.logging.Level;
import java.util.logging.Logger;
import rowdy.BaseNode;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import rowdy.Function;
import rowdy.SymbolTable;
import static rowdy.lang.RowdyGrammarConstants.CONST_OPT;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;
import static rowdy.lang.RowdyGrammarConstants.GLOBAL_DEF;
import static rowdy.lang.RowdyGrammarConstants.ID;
import static rowdy.lang.RowdyGrammarConstants.ID_ACCESS;
import static rowdy.lang.RowdyGrammarConstants.ID_MODIFIER;
import static rowdy.lang.RowdyGrammarConstants.THIS_REF;

/**
 *
 * @author Richard
 */
public class AssignStatement extends BaseNode {
  
  public AssignStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) {
    try {
      Terminal idTerminal = (Terminal) get(ID).symbol();
      BaseNode assignExpr = get(EXPRESSION);
      Value rightValue = assignExpr.execute();
      
      if (rightValue.isConstant()) {
        rightValue.setAsConstant(false);
      }
      RowdyNode idModifier = (RowdyNode) get(ID_MODIFIER);
      if (idModifier.hasSymbols()) {
        switch (idModifier.getLeftMost().symbol().id()) {
          case CONST_OPT:
            rightValue.setAsConstant(true);
            break;
            
        }
      }
      
      RowdyNode idAccess = (RowdyNode) get(ID_ACCESS);
      if (idAccess.hasSymbols()) {
        switch(idAccess.getLeftMost().symbol().id()) {
          case THIS_REF:
            Function curFunction = instance.callStack.peek();
            SymbolTable table;
            if (curFunction.isMemberFunction()) {
              table = curFunction.getParent().getSymbolTable();
            } else {
              table = curFunction.getSymbolTable();
            }
            table.allocate(idTerminal, rightValue, getLine());
            return null;
          case GLOBAL_DEF:
            instance.setAsGlobal(idTerminal, rightValue);
            return null;
        }
      }
      instance.allocate(idTerminal, rightValue, getLine());
    } catch (ConstantReassignmentException ex) {
      Logger.getLogger(AssignStatement.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }
  
}
