
package rowdy.nodes.statement;

import growdy.Symbol;
import growdy.Terminal;
import rowdy.BaseNode;
import rowdy.Function;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.ID;
import static rowdy.lang.RowdyGrammarConstants.STMT_LIST;

/**
 *
 * @author Richard
 */
public class LoopStatement extends BaseNode {
  
  public LoopStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Object execute(Object leftValue) {
    Terminal loopIdTerminal = (Terminal) get(ID).symbol();
    String idName = (loopIdTerminal).getValue();
    Function curFunction = null;
    try {
      if (!instance.callStack.isEmpty()) {
        curFunction = instance.callStack.peek();
        Value curValue = curFunction.getSymbolTable().getValue(idName);
        if (curValue == null) {
          curFunction.getSymbolTable().allocate(idName, new Value(0, false), this.getLine(), false);
        } else {
          throw new RuntimeException("ID '" + idName + "' already in use "
                  + "on line " + getLine());
        }
      } else {
        instance.allocate(idName, new Value(0, false), this.getLine());
      }
      instance.activeLoops.push(this);
      setSeqActive(true);
      boolean done = false;
      BaseNode loopStmtList = get(STMT_LIST);
      while (!done) {
        instance.executeStmt(loopStmtList, this);
        done = !isSeqActive();
      }
      if (curFunction != null) {
        curFunction.getSymbolTable().unset(idName);
      }
    } catch (ConstantReassignmentException | RuntimeException ex) {
      throw new RuntimeException(ex);
    }
    return null;
  }
}
