
package rowdy.nodes.statement;

import growdy.Node;
import growdy.Symbol;
import growdy.Terminal;
import rowdy.Function;
import rowdy.Value; 
import rowdy.nodes.RowdyNode;
import static rowdy.lang.RowdyGrammarConstants.ID;
import static rowdy.lang.RowdyGrammarConstants.ID_OPTION;


/**
 *
 * @author Richard
 */
public class BreakStatement extends RowdyNode {
  
  public BreakStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) {
    String idName;
    if (!get(ID_OPTION).hasSymbols()) {
      if (instance.activeLoops.isEmpty()) {
        throw new RuntimeException("No loop to break. Line " 
                + getLine());
      }
      Node loopId = instance.activeLoops.peek();
      idName = ((Terminal) loopId.get(ID).symbol()).getName();
    } else {
      idName = ((Terminal) get(ID_OPTION).get(ID).symbol()).getName();
    }
    Function curFunction = null;
    if (!instance.callStack.isEmpty()) {
      curFunction = instance.callStack.peek();
      if (curFunction.getSymbolTable().getValue(idName) == null) {
        throw new RuntimeException("The ID '" + idName + "' doesn't exist."
                + " Line " + getLine());
      }
    }
    for (;;) {
      Node lp = instance.activeLoops.pop();
      lp.setSeqActive(false);
      String tempBinding = ((Terminal) lp.get(ID).symbol()).getName();
      if (curFunction != null){
        curFunction.getSymbolTable().unset(tempBinding);
      } else {
        instance.globalSymbolTable.remove(idName);
      }
      if (idName.equals(tempBinding)) {
        break;
      }
    }
    return null;
  }
}