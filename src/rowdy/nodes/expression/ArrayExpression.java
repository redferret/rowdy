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

  public ArrayExpression(Symbol def, int lineNumber, RowdyRunner runner) {
    super(def, lineNumber, runner);
  }

  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    Node arrayExpression = getLeftMost();
    Value firstValue = runner.getValue(arrayExpression.get(EXPRESSION));
    Node arrayBody = arrayExpression.get(ARRAY_BODY);

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
      Value keyValue = runner.getValue(bodyType.get(EXPRESSION));
      keypairArray.put(key.getValue().toString(), keyValue.getValue());
      arrayBody = bodyType;
      Node bodyTail = arrayBody.get(ARRAY_KEY_VALUE_BODY_TAIL, false);
      arrayBody = bodyTail.get(ARRAY_KEY_VALUE_BODY, false);
      while (arrayBody != null && bodyType != null) {
        key = runner.getValue(bodyTail.get(EXPRESSION));
        keyValue = runner.getValue(arrayBody.get(EXPRESSION));
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
          arrayValue = runner.getValue(arrayBody.get(EXPRESSION));
          arrayBody = arrayBody.get(ARRAY_LINEAR_BODY, false);
        }
      }
      return new Value(array);
    }
  }

}
