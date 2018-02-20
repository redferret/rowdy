
package rowdy.nodes.statement;

import growdy.Node;
import growdy.Symbol;
import growdy.Terminal;
import rowdy.Function;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.ID;
import static rowdy.lang.RowdyGrammarConstants.STMT_BLOCK;
import static rowdy.lang.RowdyGrammarConstants.STMT_LIST;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class LoopStatement extends RowdyNode {
  
  public LoopStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    Terminal loopIdTerminal = (Terminal) get(ID).symbol();
    String idName = (loopIdTerminal).getName();
    Function curFunction = null;
    if (!instance.callStack.isEmpty()) {
      curFunction = instance.callStack.peek();
      Value curValue = curFunction.getValue(idName);
      if (curValue == null) {
        curFunction.allocate(idName, new Value(0, false));
      } else {
        throw new RuntimeException("ID '" + idName + "' already in use "
                + "on line " + getLine());
      }
    } else {
      instance.allocate(loopIdTerminal, new Value(0, false));
    }
    instance.activeLoops.push(this);
    setSeqActive(true);
    boolean done = false;
    Node loopStmtList = get(STMT_BLOCK).get(STMT_LIST);
    while (!done) {
      instance.executeStmt(loopStmtList, this);
      done = !isSeqActive();
    }
    if (curFunction != null) {
      curFunction.unset(idName);
    }
    return null;
  }
}