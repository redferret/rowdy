
package rowdy.nodes.expression;

import growdy.Symbol;
import java.util.ArrayList;
import java.util.List;
import rowdy.BaseNode;
import rowdy.RowdyClass;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.ARRAY_EXPR;
import static rowdy.lang.RowdyGrammarConstants.FUNC_BODY_EXPR;
import static rowdy.lang.RowdyGrammarConstants.ID;
import static rowdy.lang.RowdyGrammarConstants.ID_;
import static rowdy.lang.RowdyGrammarConstants.MAP_EXPR;
import static rowdy.lang.RowdyGrammarConstants.OBJ_OR_ARRAY;

/**
 *
 * @author Richard
 */
public class NewObject extends BaseNode {

  public NewObject(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }

  @Override
  public Object execute(Object leftValue) {
    BaseNode newType = get(OBJ_OR_ARRAY).getLeftMost();
    switch(newType.symbol().id()) {
      case ARRAY_EXPR:
      case MAP_EXPR:
        return newType.execute();
      case ID_:
        BaseNode idNode = newType.get(ID);
        RowdyClass rowdyClass = (RowdyClass) instance.fetch(instance.getIdAsValue(idNode), this).getValue();

        List<Value> constructorParams = new ArrayList<>();

        BaseNode funcBodyExpr = get(FUNC_BODY_EXPR);
        if (funcBodyExpr != null) {
          Value paramsValue = (Value) funcBodyExpr.execute(new Value(new ArrayList<>()));
          List<BaseNode> params = (List<BaseNode>) paramsValue.getValue();

          params.forEach((expression) -> {
            Value v = (Value) expression.execute();
            v.setAsConstant(false);
            constructorParams.add(v);
          });
        }
        return new Value(rowdyClass.getInstance(constructorParams), false);
      default:
        throw new RuntimeException("Undefined type to be created on line " + getLine());
    }
    
  }
  
}
