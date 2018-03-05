
package rowdy.nodes.expression;

import growdy.Symbol;
import growdy.Terminal;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.CONSTANT;

/**
 *
 * @author Richard
 */
public class AtomicConst extends BaseNode {

  public AtomicConst(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }

  @Override
  public Value execute(Value leftValue) {
    BaseNode child = get(CONSTANT);
    Terminal atomicTerminal = ((Terminal) child.symbol());
    String val = atomicTerminal.getName();
    int len = val.length();
    Object newValue;
    if (val.contains("\"")) {
      newValue = val.replaceAll("\"", "");
    } else if (val.contains(".") || val.charAt(len - 1) == 'd' || val.charAt(len - 1) == 'D') {
      try {
        newValue = Double.parseDouble(val);
      } catch (NumberFormatException e) {
        newValue = val;
      }
    } else if (val.charAt(len - 1) == 'l' || val.charAt(len - 1) == 'L') {
      try {
        String sub = val.substring(0, len - 1);
        newValue = Long.parseLong(sub);
      } catch (NumberFormatException e) {
        newValue = val;
      }
    } else {
      try {
        newValue = Integer.parseInt(val);
      } catch (NumberFormatException e) {
        newValue = val;
      }
    }
    return new Value(newValue, false);
  }

}
