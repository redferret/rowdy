
package rowdy.nodes.statement;

import growdy.Node;
import growdy.Symbol;
import growdy.Terminal;
import rowdy.BaseNode;
import rowdy.Function;
import rowdy.nodes.RowdyNode;
import static rowdy.lang.RowdyGrammarConstants.ID;
import static rowdy.lang.RowdyGrammarConstants.ID_OPTION;
import static rowdy.lang.RowdyGrammarConstants.WHILE_LOOP;


/**
 *
 * @author Richard
 */
public class BreakStatement extends RowdyNode {
  
  public BreakStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Object execute(Object leftValue) {
    String idName = "", breakId = "";
    BaseNode idOption = get(ID_OPTION);
    Function currentFunc = instance.callStack.peek();
    if (idOption == null) {
      if (currentFunc.activeLoops.isEmpty()) {
        throw new RuntimeException("No loop to break. Line " 
                + getLine());
      }
      Node loopId = currentFunc.activeLoops.peek();
      if (loopId.symbol().id() != WHILE_LOOP) {
        idName = ((Terminal) loopId.get(ID).symbol()).getValue();
      }
    } else {
      breakId = ((Terminal) get(ID_OPTION).get(ID).symbol()).getValue();
    }
    Function curFunction = null;
    if (!idName.isEmpty()) {
      if (!instance.callStack.isEmpty()) {
        curFunction = instance.callStack.peek();
        if (curFunction.getSymbolTable().getValue(idName) == null) {
          throw new RuntimeException("The ID '" + idName + "' doesn't exist."
                  + " Line " + getLine());
        }
      }
    }
    while (!currentFunc.activeLoops.isEmpty()) {
      Node lp = currentFunc.activeLoops.peek();
      lp.setSeqActive(false);
      if (lp.symbol().id() != WHILE_LOOP) {
        idName = ((Terminal) lp.get(ID).symbol()).getValue();
        currentFunc.activeLoops.pop();
      } else {
        break;
      }
      if (!idName.isEmpty()) {
        if (curFunction != null){
          curFunction.getSymbolTable().unset(idName);
        } else {
          instance.globalSymbolTable.remove(idName);
        }
        if (idName.equals(breakId)) {
          break;
        }
      } else {
        break;
      }
    }
    return null;
  }
}
