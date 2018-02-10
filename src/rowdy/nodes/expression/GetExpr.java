
package rowdy.nodes.expression;

import growdy.Node;
import growdy.Symbol;
import java.util.HashMap;
import java.util.List;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;

/**
 *
 * @author Richard
 */
public class GetExpr extends RowdyNode {

  public GetExpr(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }

  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    Node getExpr = getLeftMost();
    Value array = runner.getValue(getExpr.get(EXPRESSION));
    if (array.getValue() instanceof List){
      List<Object> list = (List<Object>)array.getValue();
      Object arrayIndexValue = runner.getValue(getExpr.get(EXPRESSION, 1)).getValue();
      int index;
      if (arrayIndexValue instanceof Integer){
        index = (Integer)arrayIndexValue;
      }else if (arrayIndexValue instanceof Double){
        index = ((Double)arrayIndexValue).intValue();
      } else {
        String strRep = (String) arrayIndexValue;
        index = Integer.parseInt(strRep);
      }
      return new Value(list.get(index));
    } else {
      HashMap<String, Object> map = (HashMap)array.getValue();
      Value key = runner.getValue(getExpr.get(EXPRESSION, 1));
      Value keyValue = new Value(map.get(key.getValue().toString()));
      return keyValue;
    }
  }
  
}
