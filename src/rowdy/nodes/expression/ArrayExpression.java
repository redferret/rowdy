package rowdy.nodes.expression;

import growdy.Symbol;
import java.util.ArrayList;
import java.util.List;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.ARRAY_BODY;

/**
 *
 * @author Richard
 */
public class ArrayExpression extends BaseNode {

  public ArrayExpression(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }

  @Override
  public Object execute(Object leftValue){
    BaseNode firstValueNode = getLeftMost();
    
    if (firstValueNode == null) {
      return new Value(new ArrayList<>(), false);
    }
    
    Value firstValue = (Value) firstValueNode.execute();
    BaseNode arrayBody = get(ARRAY_BODY);

    if (arrayBody == null) {
      List<Object> arrayList = new ArrayList<>();
      arrayList.add(firstValue.getValue());
      return new Value(arrayList, false);
    }
    
    List<Object> array = new ArrayList<>();
    Value arrayValue = firstValue;
    while (arrayValue != null) {
      array.add(arrayValue.getValue());
      arrayValue = null;
      if (arrayBody != null && arrayBody.hasSymbols()) {
        BaseNode bodyTypeExpr = arrayBody.getLeftMost();
        arrayValue = (Value) bodyTypeExpr.execute();
        arrayBody = arrayBody.get(ARRAY_BODY, false);
      }
    }
    return new Value(array, false);
  }

}
