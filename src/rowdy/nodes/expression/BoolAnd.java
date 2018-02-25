
package rowdy.nodes.expression;

import growdy.Symbol;
import java.util.ArrayList;
import rowdy.BaseRowdyNode;
import rowdy.Value;
import static rowdy.lang.RowdyGrammarConstants.BOOL_FACTOR;
import static rowdy.lang.RowdyGrammarConstants.BOOL_FACTOR_TAIL;

/**
 *
 * @author Richard
 */
public class BoolAnd extends BaseRowdyNode {
  
  public BoolAnd(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) {
    ArrayList<BaseRowdyNode> boolChildren = getAll();
    if (boolChildren.isEmpty()) {
      return instance.fetch(leftValue, this);
    }
    leftValue = instance.fetch(leftValue, this);
    boolean bLeft = leftValue.valueToBoolean();
    BaseRowdyNode boolFactor = get(BOOL_FACTOR);
    boolean bRight = boolFactor.execute(leftValue).valueToBoolean();
    BoolAnd boolFactorTail = (BoolAnd) get(BOOL_FACTOR_TAIL);
    
    return boolFactorTail.execute(new Value(bLeft && bRight, false));
  }
}
