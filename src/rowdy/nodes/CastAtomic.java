
package rowdy.nodes;

import growdy.Symbol;
import java.math.BigInteger;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC;
import static rowdy.lang.RowdyGrammarConstants.CAST_BINT_OPT;
import static rowdy.lang.RowdyGrammarConstants.CAST_BOL_OPT;
import static rowdy.lang.RowdyGrammarConstants.CAST_BYT_OPT;
import static rowdy.lang.RowdyGrammarConstants.CAST_INT_OPT;
import static rowdy.lang.RowdyGrammarConstants.CAST_LNG_OPT;
import static rowdy.lang.RowdyGrammarConstants.CAST_OPT;
import static rowdy.lang.RowdyGrammarConstants.CAST_SHRT_OPT;
import static rowdy.lang.RowdyGrammarConstants.CAST_STR_OPT;
import rowdy.nodes.expression.Atomic;

/**
 *
 * @author Richard
 */
public class CastAtomic extends RowdyNode {
  
  public CastAtomic(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    
    Atomic atomic = (Atomic)get(ATOMIC);
    Value castValue = runner.fetch(atomic.execute(), this);
    RowdyNode castOpt = (RowdyNode) get(CAST_OPT).getLeftMost();
    if (castOpt != null) {
      switch (castOpt.symbol().id()) {
        case CAST_STR_OPT:
          String newStrVal = castValue.getValue().toString();
          castValue = new Value(newStrVal, false);
          break;
        case CAST_BINT_OPT:
          BigInteger bigInt = new BigInteger(castValue.getValue().toString());
          castValue = new Value(bigInt, false);
          break;
        case CAST_INT_OPT:
          Integer newInt = castValue.valueToDouble().intValue();
          castValue = new Value(newInt, false);
          break;
        case CAST_BOL_OPT:
          Boolean newBool = (Boolean) castValue.getValue();
          castValue = new Value(newBool, false);
          break;
        case CAST_BYT_OPT:
          Byte newByte = (Byte) castValue.getValue();
          castValue = new Value(newByte, false);
          break;
        case CAST_SHRT_OPT:
          Short newShrt = (Short) castValue.getValue();
          castValue = new Value(newShrt, false);
          break;
        case CAST_LNG_OPT:
          Long newLong = (Long) castValue.getValue();
          castValue = new Value(newLong, false);
          break;
      }
    }
    return castValue;
  }
}
