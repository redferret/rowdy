
package rowdy.nodes.expression;

import growdy.Symbol;
import java.util.ArrayList;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.BOOL_TERM;
import static rowdy.lang.RowdyGrammarConstants.BOOL_TERM_TAIL;

/**
 *
 * @author Richard
 */
public class BoolOr extends BaseNode {
  
  public BoolOr(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  
  @Override
  public Value execute(Value leftValue) {
    ArrayList<BaseNode> boolChildren = getAll();
    if (boolChildren.isEmpty()) {
      return instance.fetch(leftValue, this);
    }
    leftValue = instance.fetch(leftValue, this);
    boolean bLeft = leftValue.valueToBoolean();
    
    if (bLeft == true) {
      return new Value(true, false);
    }
    
    BaseNode boolTerm = get(BOOL_TERM);
    boolean bRight = boolTerm.execute(leftValue).valueToBoolean();
    BoolOr boolTermTail = (BoolOr) get(BOOL_TERM_TAIL);
    
    return boolTermTail.execute(new Value(bLeft || bRight, false));
  }
}
