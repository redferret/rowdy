
package rowdy.nodes.statement;

import growdy.Node;
import growdy.Symbol;
import growdy.Terminal;
import java.io.InputStream;
import java.util.Scanner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.ID;
import static rowdy.lang.RowdyGrammarConstants.PARAMS_TAIL;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public class ReadStatement extends RowdyNode {
  
  public ReadStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }

  @Override
  public Value execute(Value inputStreamWrapper) throws ConstantReassignmentException {
    Scanner keys = new Scanner((InputStream) inputStreamWrapper.getValue());
    Node firstID = get(ID);
    Terminal t = (Terminal) firstID.symbol();
    instance.allocate(t, new Value(keys.nextLine(), false));
    if (hasSymbols()) {
      Node paramsTail = get(PARAMS_TAIL);
      while (paramsTail.hasSymbols()) {
        RowdyNode idNode = (RowdyNode) paramsTail.get(ID);
        t = (Terminal) idNode.symbol();
        instance.allocate(t, new Value(keys.nextLine(), false));
        paramsTail = paramsTail.get(PARAMS_TAIL);
      }
    }
    return null;
  }
}
