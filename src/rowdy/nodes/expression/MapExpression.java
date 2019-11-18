
package rowdy.nodes.expression;

import growdy.Symbol;
import java.util.HashMap;
import java.util.List;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC_ID;
import static rowdy.lang.RowdyGrammarConstants.MAP_BODY;
import static rowdy.lang.RowdyGrammarConstants.MAP_ELEMENT;
import static rowdy.lang.RowdyGrammarConstants.MAP_EXPR;

/**
 *
 * @author Richard
 */
public class MapExpression extends BaseNode {

  public MapExpression(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }

  @Override
  public Object execute(Object leftValue) {
    BaseNode mapElement = getLeftMost();
    HashMap map = new HashMap();
    
    if (mapElement == null) {
      return new Value(map, false);
    }
    BaseNode mapBody = get(MAP_BODY);
    
    while(mapElement != null && mapElement.hasSymbols()) {
      BaseNode keyNode = mapElement.get(ATOMIC_ID, 0);
      BaseNode valueNode = mapElement.get(ATOMIC_ID, 1, false);
      Value key, value;
      
      key = (Value) keyNode.execute();
      if (valueNode == null) {
        value = (Value) mapElement.get(MAP_EXPR).execute();
      } else {
        value = (Value) valueNode.execute();
      }
      
      map.put(key.getValue(), value.getValue());
      if (mapBody == null) {
        break;
      }
      mapElement = mapBody.get(MAP_ELEMENT);
      mapBody = mapBody.get(MAP_BODY);
    }
    return new Value(map, false);
  }
  
}
