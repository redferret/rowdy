
package rowdy.nodes.expression;

import growdy.Symbol;
import java.math.BigInteger;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.nodes.RowdyNode;
import static rowdy.lang.RowdyGrammarConstants.*;

/**
 *
 * @author Richard
 */
public class Expression extends RowdyNode {

  public Expression(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    Expressions exprs = (Expressions) get(EXPRESSIONS);
    
    RowdyNode castAs = (RowdyNode) get(CAST_AS);
    Value castValue = runner.fetch(exprs.execute(), this); 
    RowdyNode castOpt = (RowdyNode) castAs.get(CAST_OPT, false); 
    if (castOpt != null) { 
      RowdyNode castType = (RowdyNode) castOpt.getLeftMost();
      switch (castType.symbol().id()) { 
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
