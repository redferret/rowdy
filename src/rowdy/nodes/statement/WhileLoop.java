
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
public class WhileLoop extends BaseNode {

  public WhileLoop(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }

  @Override
  public Object execute(Object leftValue) {
    try {
      BaseNode loopTest = getLeftMost();
      instance.activeLoops.push(this);
      setSeqActive(true);
      boolean done = false;
      BaseNode loopStmtList = get(STMT_LIST);
      while (!done) {
        instance.executeStmt(loopStmtList, this);
        boolean testBool = !(boolean) ((Value)loopTest.execute()).getValue();
        done = !isSeqActive() || testBool;
      }
    } catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
    return null;
  }
  
}
