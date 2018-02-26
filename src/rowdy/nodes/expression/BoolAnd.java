
package rowdy.nodes.expression;

import growdy.Symbol;
import java.util.ArrayList;
import rowdy.BaseNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.BOOL_FACTOR;
import static rowdy.lang.RowdyGrammarConstants.BOOL_FACTOR_TAIL;

/**
 *
 * @author Richard
 */
public class BoolAnd extends BaseNode {
  
  public BoolAnd(Symbol def, int lineNumber) {
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
    BaseNode boolFactor = get(BOOL_FACTOR);
    boolean bRight = boolFactor.execute(leftValue).valueToBoolean();
    BoolAnd boolFactorTail = (BoolAnd) get(BOOL_FACTOR_TAIL);
    
    return boolFactorTail.execute(new Value(bLeft && bRight, false));
  }
}
