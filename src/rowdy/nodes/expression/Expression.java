
package rowdy.nodes.expression;

import growdy.Symbol;
import java.math.BigInteger;
import rowdy.BaseNode;
import rowdy.Value;
import rowdy.nodes.RowdyNode;
import static rowdy.lang.RowdyGrammarConstants.*;

/**
 *
 * @author Richard
 */
public class Expression extends BaseNode {

  public Expression(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) {
    BaseNode leftNode = getLeftMost();
    if (leftNode == null) {
      return instance.fetch(leftValue, this);
    }
    RowdyNode castAs = (RowdyNode) get(CAST_AS, false);
    Value castValue = instance.fetch(leftNode.execute(), this); 
    if (castAs != null) { 
      RowdyNode castOpt = (RowdyNode) castAs.get(CAST_OPT, false);
      if (castOpt != null) {
        RowdyNode castType = (RowdyNode) castOpt.getLeftMost();
        switch (castType.symbol().id()) { 
          case CAST_STR_OPT: 
            String newStrVal = castValue.getValue().toString(); 
            castValue = new Value(newStrVal, false); 
            break; 
          case CAST_DBL_OPT:
            Object vtodbl = castValue.getValue();
            Double newDouble;
            if (vtodbl instanceof Number) {
              newDouble = ((Number)vtodbl).doubleValue();
            } else if (vtodbl instanceof Boolean) {
              newDouble = ((Boolean)vtodbl)? 1d : 0d;
            } else if (vtodbl instanceof String) {
              newDouble = Double.parseDouble(vtodbl.toString());
            } else {
              newDouble = Double.NaN;
            }
            castValue = new Value(newDouble, false); 
            break; 
          case CAST_BINT_OPT: 
            Object bigInt = castValue.getValue();
            BigInteger nBigInt;
            if (bigInt instanceof Number || bigInt instanceof String) {
              nBigInt = new BigInteger(castValue.getValue().toString()); 
            } else if (bigInt instanceof Boolean) {
              nBigInt = new BigInteger(Integer.toString(((Boolean)bigInt)? 1 : 0)); 
            } else {
              nBigInt = new BigInteger(Integer.toString(castValue.getValue().hashCode())); 
            }
            castValue = new Value(nBigInt, false); 
            break;
          case CAST_INT_OPT: 
            Object vtoint = castValue.getValue();
            Integer newInt;
            if (vtoint instanceof Number) {
              newInt = ((Number)vtoint).intValue();
            } else if (vtoint instanceof Boolean) {
              newInt = ((Boolean)vtoint)? 1 : 0;
            } else if (vtoint instanceof String) {
              Double possD = Double.parseDouble(vtoint.toString());
              newInt = possD.intValue();
            } else {
              newInt = vtoint.hashCode();
            }
            castValue = new Value(newInt, false); 
            break; 
          case CAST_BOL_OPT: 
            Boolean newBool;
            Object toBool = castValue.getValue();
            if (toBool instanceof Number) {
              float pp = ((Number)toBool).floatValue();
              newBool = pp > 0 ? true : (pp < 0);
            } else if (toBool instanceof Boolean) {
              newBool = ((Boolean)toBool);
            } else if (toBool instanceof String) {
              newBool = Boolean.parseBoolean(toBool.toString());
            } else {
              newBool = true;
            }
            castValue = new Value(newBool, false); 
            break; 
          case CAST_BYT_OPT: 
            Object vtoByte = castValue.getValue();
            Byte nByte;
            if (vtoByte instanceof Number) {
              nByte = ((Number)vtoByte).byteValue();
            } else if (vtoByte instanceof Boolean) {
              nByte = ((Boolean)vtoByte)? (byte)1 : (byte)0;
            } else if (vtoByte instanceof String) {
              nByte = Byte.parseByte(vtoByte.toString());
            } else {
              nByte = 0;
            }
            castValue = new Value(nByte, false); 
            break; 
          case CAST_SHRT_OPT: 
            Object vtoShort = castValue.getValue();
            Short nShort;
            if (vtoShort instanceof Number) {
              nShort = ((Number)vtoShort).shortValue();
            } else if (vtoShort instanceof Boolean) {
              nShort = ((Boolean)vtoShort)? (short)1 : (short)0;
            } else if (vtoShort instanceof String) {
              nShort = Short.parseShort(vtoShort.toString());
            } else {
              nShort = 0;
            }
            castValue = new Value(nShort, false); 
            break; 
          case CAST_LNG_OPT: 
            Object vtoLong = castValue.getValue();
            Long nLong;
            if (vtoLong instanceof Number) {
              nLong = ((Number)vtoLong).longValue();
            } else if (vtoLong instanceof Boolean) {
              nLong = ((Boolean)vtoLong)? (long)1 : (long)0;
            } else if (vtoLong instanceof String) {
              nLong = Long.parseLong(vtoLong.toString());
            } else {
              nLong = (long)(vtoLong.hashCode());
            }
            castValue = new Value(nLong, false); 
            break; 
        } 
      }
    } 
    return castValue; 
  }
}
