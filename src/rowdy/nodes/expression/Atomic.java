
package rowdy.nodes.expression;

import growdy.Symbol;
import growdy.Terminal;
import java.math.BigInteger;
import rowdy.nodes.RowdyNode;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC_CONST;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC_FUNC_CALL;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC_ID;
import static rowdy.lang.RowdyGrammarConstants.CAST_BINT_OPT;
import static rowdy.lang.RowdyGrammarConstants.CAST_BOL_OPT;
import static rowdy.lang.RowdyGrammarConstants.CAST_BYT_OPT;
import static rowdy.lang.RowdyGrammarConstants.CAST_INT_OPT;
import static rowdy.lang.RowdyGrammarConstants.CAST_LNG_OPT;
import static rowdy.lang.RowdyGrammarConstants.CAST_OPT;
import static rowdy.lang.RowdyGrammarConstants.CAST_SHRT_OPT;
import static rowdy.lang.RowdyGrammarConstants.CAST_STR_OPT;
import static rowdy.lang.RowdyGrammarConstants.CONSTANT;
import static rowdy.lang.RowdyGrammarConstants.FUNC_CALL;
import static rowdy.lang.RowdyGrammarConstants.ID;

/**
 *
 * @author Richard
 */
public class Atomic extends RowdyNode {

  public Atomic(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    RowdyNode atomicType = (RowdyNode) getLeftMost();
    RowdyNode child;
    Value castValue = new Value();
    switch(atomicType.symbol().id()) {
      case ATOMIC_ID:
        child = (RowdyNode) atomicType.get(ID);
        castValue = new Value(((Terminal) child.symbol()).getName());
        break;
      case ATOMIC_CONST:
        child = (RowdyNode) atomicType.get(CONSTANT);
        castValue = new Value(((Terminal) child.symbol()).getName());
        break;
      case ATOMIC_FUNC_CALL:
        child = (RowdyNode) atomicType.get(FUNC_CALL);
        castValue = runner.executeFunc(child);
        break;
    }
    
    // Place a switch on child's left most to find the Atomic to get
    
    RowdyNode castOpt = (RowdyNode) atomicType.get(CAST_OPT).getLeftMost();
    if (castOpt != null) {
      RowdyNode castType = (RowdyNode) castOpt.getLeftMost();
      switch (castType.symbol().id()) {
        case CAST_STR_OPT:
          String newStrVal = castValue.getValue().toString();
          castValue = new Value(newStrVal);
          break;
        case CAST_BINT_OPT:
          BigInteger bigInt = new BigInteger(castValue.getValue().toString());
          castValue = new Value(bigInt);
          break;
        case CAST_INT_OPT:
          Integer newInt = castValue.valueToDouble().intValue();
          castValue = new Value(newInt);
          break;
        case CAST_BOL_OPT:
          Boolean newBool = (Boolean) castValue.getValue();
          castValue = new Value(newBool);
          break;
        case CAST_BYT_OPT:
          Byte newByte = (Byte) castValue.getValue();
          castValue = new Value(newByte);
          break;
        case CAST_SHRT_OPT:
          Short newShrt = (Short) castValue.getValue();
          castValue = new Value(newShrt);
          break;
        case CAST_LNG_OPT:
          Long newLong = (Long) castValue.getValue();
          castValue = new Value(newLong);
          break;
      }
    }
    return castValue;
  }
}
