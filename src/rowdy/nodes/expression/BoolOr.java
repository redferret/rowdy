
package rowdy.nodes.expression;

import growdy.Symbol;
import java.util.ArrayList;
import rowdy.BaseNode;
import rowdy.Value;
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
  public Object execute(Object leftValue) {
    ArrayList<BaseNode> boolChildren = getAll();
    if (boolChildren.isEmpty()) {
      return instance.fetch((Value) leftValue, this);
    }
    leftValue = instance.fetch((Value) leftValue, this);
    boolean bLeft = (boolean)((Value)leftValue).getValue();
    
    if (bLeft == true) {
      return new Value(true, false);
    }
    
    BaseNode boolTerm = getLeftMost();
    boolean bRight = (boolean) ((Value)boolTerm.execute(leftValue)).getValue();
    BoolOr boolTermTail = (BoolOr) get(BOOL_TERM_TAIL);
    
    if (boolTermTail == null) {
      return new Value(bLeft || bRight, false);
    }
    
    return boolTermTail.execute(new Value(bLeft || bRight, false));
  }
}
