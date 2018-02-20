
package rowdy.nodes.statement;

import growdy.Node;
import growdy.Symbol;
import java.io.PrintStream;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;
import static rowdy.lang.RowdyGrammarConstants.EXPR_LIST;
import rowdy.nodes.RowdyNode;
import rowdy.nodes.expression.Expression;

/**
 *
 * @author Richard
 */
public class PrintStatement extends RowdyNode {
  
  public PrintStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value printStreamWrapper) throws ConstantReassignmentException {
    PrintStream stream = (PrintStream) printStreamWrapper.getValue();
    StringBuilder printValue = new StringBuilder();
    Expression printValExpr = (Expression) get(EXPRESSION);
    Value printVal = printValExpr.execute();
    if (printVal == null) {
      printValue.append("null");
    } else {
      printValue.append(printVal.valueToString());
    }
    Node atomTailNode = get(EXPR_LIST);
    while (atomTailNode.hasSymbols()) {
      printValExpr = (Expression) atomTailNode.get(EXPRESSION);
      printVal = printValExpr.execute();
      if (printVal == null) {
        printValue.append("null");
      } else {
        printValue.append(printVal.valueToString());
      }
      atomTailNode = atomTailNode.get(EXPR_LIST);
    }
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
