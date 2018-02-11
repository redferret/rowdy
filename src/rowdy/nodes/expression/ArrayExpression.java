package rowdy.nodes.expression;

import growdy.Node;
import growdy.Symbol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import static rowdy.lang.RowdyGrammarConstants.ARRAY_BODY;
import static rowdy.lang.RowdyGrammarConstants.ARRAY_KEY_VALUE_BODY;
import static rowdy.lang.RowdyGrammarConstants.ARRAY_KEY_VALUE_BODY_TAIL;
import static rowdy.lang.RowdyGrammarConstants.ARRAY_LINEAR_BODY;
import static rowdy.lang.RowdyGrammarConstants.EXPRESSION;

/**
 *
 * @author Richard
 */
public class ArrayExpression extends RowdyNode {

  public ArrayExpression(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }

  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    Expression arrayExpression = (Expression) get(EXPRESSION);
    Value firstValue = arrayExpression.execute();
    Node arrayBody = get(ARRAY_BODY);

    Node bodyType = arrayBody.get(ARRAY_LINEAR_BODY, false);
    if (bodyType == null) {
      bodyType = arrayBody.get(ARRAY_KEY_VALUE_BODY, false);

      if (bodyType == null) {
        List<Object> arrayList = new ArrayList<>();
        if (firstValue != null) {
          arrayList.add(firstValue.getValue());
        }
        return new Value(arrayList);
      }

      HashMap<String, Object> keypairArray = new HashMap<>();
      Value key = firstValue;
      Expression bodyTypeExpr = (Expression) bodyType.get(EXPRESSION);
      Value keyValue = bodyTypeExpr.execute();
      keypairArray.put(key.getValue().toString(), keyValue.getValue());
      arrayBody = bodyType;
      Node bodyTail = arrayBody.get(ARRAY_KEY_VALUE_BODY_TAIL, false);
      arrayBody = bodyTail.get(ARRAY_KEY_VALUE_BODY, false);
      while (arrayBody != null && bodyType != null) {
        Expression keyExpr = (Expression) bodyTail.get(EXPRESSION);
        key = keyExpr.execute();
        bodyTypeExpr = (Expression) arrayBody.get(EXPRESSION); 
        keyValue = bodyTypeExpr.execute();
        keypairArray.put(key.getValue().toString(), keyValue.getValue());
        bodyTail = arrayBody.get(ARRAY_KEY_VALUE_BODY_TAIL, false);
        arrayBody = bodyTail.get(ARRAY_KEY_VALUE_BODY, false);
      }
      return new Value(keypairArray);

    } else {
      List<Object> array = new ArrayList<>();
      Value arrayValue = firstValue;
      arrayBody = bodyType;
      while (arrayValue != null) {
        array.add(arrayValue.getValue());
        arrayValue = null;
        if (arrayBody != null && arrayBody.hasSymbols()) {
          Expression bodyTypeExpr = (Expression) bodyType.get(EXPRESSION);
          arrayValue = bodyTypeExpr.execute();
          arrayBody = arrayBody.get(ARRAY_LINEAR_BODY, false);
        }
      }
      return new Value(array);
    }
  }

}
