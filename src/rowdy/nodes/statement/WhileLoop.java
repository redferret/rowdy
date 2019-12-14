
package rowdy.nodes.statement;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Function;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
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
   
      BaseNode loopTest = getLeftMost();
      Function currentFunc = instance.callStack.peek();
      currentFunc.activeLoops.push(this);
      setSeqActive(true);
      boolean done = false;
      BaseNode loopStmtList = get(STMT_LIST);
      while (!done) {
        try {
          instance.executeStmt(loopStmtList, this);
        } catch (ConstantReassignmentException ex) {
          throw new RuntimeException(ex);
        }
        boolean testBool = !(boolean) ((Value)loopTest.execute()).getValue();
        done = !isSeqActive() || testBool;
      }
    return null;
  }
  
}
