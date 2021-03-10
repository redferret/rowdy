
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
  public Object execute(Object printStream) {
    PrintStream stream = (PrintStream) printStream;
    StringBuilder printValue = new StringBuilder();
    int deleteAt = 0;
    if (instance.isShellEnv()){
      printValue.append(">> ");
      deleteAt = 3;
    }
    
    BaseNode paramsNode = get(PARAMETERS);
    Value listValue = (Value) paramsNode.execute(new Value(new ArrayList<>()));
    List<BaseNode> params = (List<BaseNode>) listValue.getValue();
    params.forEach((expression) -> {
      Value printVal = (Value) expression.execute();
      if (printVal == null) {
        printValue.append("null");
      } else {
        printValue.append(printVal.getValue());
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
      printValue.delete(deleteAt, printValue.length());
      printValue.append(toPrint);
    } 
    if (instance.isShellEnv()) {
      stream.println(printValue);
    }else {
      stream.print(printValue);
    }
    return null;
  }
}
