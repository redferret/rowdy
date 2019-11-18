
package rowdy.nodes.expression;

import growdy.Symbol;
import java.util.ArrayList;
import rowdy.BaseNode;
import rowdy.Value;
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
  public Object execute(Object leftValue) {
    ArrayList<BaseNode> boolChildren = getAll();
    if (boolChildren.isEmpty()) {
      return instance.fetch((Value) leftValue, this);
    }
    leftValue = instance.fetch((Value) leftValue, this);
    boolean bLeft = (boolean) ((Value) leftValue).getValue();
    
    if (bLeft == false){
      return new Value(false, false);
    }
    
    BaseNode boolFactor = getLeftMost();
    boolean bRight = (boolean) ((Value)boolFactor.execute(leftValue)).getValue();
    BoolAnd boolFactorTail = (BoolAnd) get(BOOL_FACTOR_TAIL);
    
    if (boolFactorTail == null) {
      return new Value(bLeft && bRight, false);
    }
    
    return boolFactorTail.execute(new Value(bLeft && bRight, false));
  }
}
