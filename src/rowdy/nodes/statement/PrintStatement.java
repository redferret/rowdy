
package rowdy.nodes.statement;

import growdy.Symbol;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.PARAMETERS;

/**
 *
 * @author Richard
 */
public class PrintStatement extends BaseNode {
  
  public PrintStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value printStreamWrapper) {
    PrintStream stream = (PrintStream) printStreamWrapper.getValue();
    StringBuilder printValue = new StringBuilder();
    
    BaseNode paramsNode = get(PARAMETERS);
    List<BaseNode> params = (List<BaseNode>) paramsNode.execute(new Value(new ArrayList<>(), false)).getValue();
    params.forEach((expression) -> {
      Value printVal = expression.execute();
      if (printVal == null) {
        printValue.append("null");
      } else {
        printValue.append(printVal.valueToString());
      }
    });
    
    char c;
    StringBuilder toPrint = new StringBuilder();
    if (printValue.toString().contains("\\n")) {
      for (int l = 0; l < printValue.length(); l++) {
        c = printValue.charAt(l);
        if ((c == '\\') && (printValue.charAt(++l) == 'n')) {
          stream.println(toPrint);
          toPrint = new StringBuilder();
        } else {
          toPrint.append(c);
        }
      }
      stream.print(toPrint);
    } else {
      stream.print(printValue);
    }
    return null;
  }
}