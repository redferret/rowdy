
package rowdy.nodes.expression;

import growdy.Node;
import growdy.Symbol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import rowdy.RowdyRunner;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.*;

/**
 *
 * @author Richard
 */
public class Expression extends Node {
  
  private RowdyRunner runner;
  
  public Expression(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  
  public Value execute(RowdyRunner runner) throws ConstantReassignmentException {
    this.runner = runner;
    return execute(this, null);
  }
  
  public Value execute(Node cur, Value leftValue) throws ConstantReassignmentException {
    Node leftChild = cur.getLeftMost();
    if (leftChild == null) {
      return leftValue;
    }
    Symbol symbolType = cur.getLeftMost().symbol();
    switch (symbolType.id()) {
      case BOOL_EXPR:
        leftChild = leftChild.getLeftMost();
      case BOOL_TERM:
        leftValue = execute(leftChild, leftValue);
        return execute(cur.get(BOOL_TERM_TAIL, false), leftValue);
      case ISSET_EXPR:
        Node issetExpr = cur.get(ISSET_EXPR);
        Value resultBoolean = new Value(runner.isset(issetExpr.get(ID)));
        return resultBoolean;
      case CONCAT_EXPR:
        StringBuilder concatValue = new StringBuilder();
        concatValue.append(execute(cur.get(EXPRESSION), leftValue).valueToString());
        Node atomTailNode = cur.get(EXPR_LIST);
        while (atomTailNode.hasSymbols()) {
          concatValue.append(execute(atomTailNode.get(EXPRESSION), leftValue).valueToString());
          atomTailNode = atomTailNode.get(EXPR_LIST);
        }
        return new Value(concatValue.toString());
      case SLICE_EXPR:
        String slice;
        slice = runner.getValue(cur.get(EXPRESSION)).valueToString();
        int leftBound = runner.getValue(cur.get(ARITHM_EXPR)).valueToDouble().intValue();
        int rightBound = runner.getValue(cur.get(ARITHM_EXPR, 1)).valueToDouble().intValue();
        return new Value(slice.substring(leftBound, rightBound));
      case STRCMP_EXPR:
        String v1,
         v2;
        v1 = runner.getValue(cur.get(EXPRESSION)).valueToString();
        v2 = runner.getValue(cur.get(EXPRESSION, 1)).valueToString();
        return new Value(v1.compareTo(v2));
      case ANONYMOUS_FUNC:
        Node anonymousFunc = cur.get(ANONYMOUS_FUNC);
        return new Value(anonymousFunc);
      case ROUND_EXPR:
        Node roundExpr = cur.get(ROUND_EXPR);
        Value valueToRound = runner.getValue(roundExpr.get(ID));
        double roundedValue = valueToRound.valueToDouble();
        int precision = runner.getValue(roundExpr.get(ARITHM_EXPR)).valueToDouble().intValue();
        double factor = 1;
        while (precision > 0) {
          factor *= 10;
          precision--;
        }
        roundedValue = (double) Math.round(roundedValue * factor) / factor;
        return new Value(roundedValue);
      case ARRAY_EXPR:
        Node arrayExpression = cur.getLeftMost();
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
          while(arrayBody != null && bodyType != null){
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
            while (arrayValue != null){
              array.add(arrayValue.getValue());
              arrayValue = null;
              if (arrayBody != null && arrayBody.hasSymbols()) {
                arrayValue = runner.getValue(arrayBody.get(EXPRESSION));
                arrayBody = arrayBody.get(ARRAY_LINEAR_BODY, false);
              }
            }
            return new Value(array);
        }
      case GET_EXPR:
        Node getExpr = cur.getLeftMost();
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
    throw new RuntimeException("Couldn't get value, "
            + "undefined Node '" + cur.getLeftMost()+"' on line " + 
            cur.getLine());
  }
  
}
