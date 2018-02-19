
package rowdy.nodes.expression;

import growdy.Symbol;
import growdy.Terminal;
import rowdy.nodes.RowdyNode;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC_CONST;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC_FUNC_CALL;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC_ID;
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
    Value value = new Value();
    switch(atomicType.symbol().id()) {
      case ATOMIC_ID:
        child = (RowdyNode) atomicType.get(ID);
        value = new Value(child.symbol(), false);
        break;
      case ATOMIC_CONST:
        child = (RowdyNode) atomicType.get(CONSTANT);
        Terminal atomicTerminal = ((Terminal) child.symbol());
        String val = atomicTerminal.getName();
        int len = val.length();
        Object newValue;
        if (val.contains("\"")){
          newValue = val.replaceAll("\"", "");
        } else if (val.contains(".") || val.charAt(len-1) == 'd' || val.charAt(len-1) == 'D'){
          try {
            newValue = Double.parseDouble(val);
          }catch (NumberFormatException e){
            newValue = val;
          }
        } else if (val.charAt(len-1) == 'l' || val.charAt(len-1) == 'L') {
          try {
            String sub = val.substring(0, len-1);
            newValue = Long.parseLong(sub);
          }catch (NumberFormatException e){
            newValue = val;
          }
        } else {
          try {
            newValue = Integer.parseInt(val);
          } catch(NumberFormatException e){
            newValue = val;
          }
        }
        value = new Value(newValue, false);
        break;
      case ATOMIC_FUNC_CALL:
        child = (RowdyNode) atomicType.get(FUNC_CALL);
        value = instance.executeFunc(child);
        break;
    }
    return value;
  }
}
