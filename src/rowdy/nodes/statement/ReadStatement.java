
package rowdy.nodes.statement;

import growdy.Node;
import growdy.Symbol;
import growdy.Terminal;
import java.io.InputStream;
import java.util.Scanner;
import rowdy.BaseNode;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import static rowdy.lang.RowdyGrammarConstants.ID;
import static rowdy.lang.RowdyGrammarConstants.PARAMS_TAIL;

/**
 *
 * @author Richard
 */
public class ReadStatement extends BaseNode {
  
  public ReadStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }

  @Override
  public Value execute(Value inputStreamWrapper) {
    try {
      Scanner keys = new Scanner((InputStream) inputStreamWrapper.getValue());
      BaseNode firstID = get(ID);
      Terminal t = (Terminal) firstID.symbol();
      instance.allocate(t, new Value(keys.nextLine(), false), this.getLine());
      if (hasSymbols()) {
        BaseNode paramsTail = get(PARAMS_TAIL);
        while (paramsTail.hasSymbols()) {
          BaseNode idNode = paramsTail.get(ID);
          t = (Terminal) idNode.symbol();
          instance.allocate(t, new Value(keys.nextLine(), false), this.getLine());
          paramsTail = paramsTail.get(PARAMS_TAIL);
        }
      }
    } catch (ConstantReassignmentException cre) {
    }
    return null;
  }
}
