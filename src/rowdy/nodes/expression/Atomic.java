
package rowdy.nodes.expression;

import growdy.Node;
import growdy.Symbol;
import growdy.Terminal;
import java.util.logging.Level;
import java.util.logging.Logger;
import rowdy.BaseNode;
import rowdy.nodes.RowdyNode;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC_CONST;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC_FUNC_CALL;
import static rowdy.lang.RowdyGrammarConstants.ATOMIC_ID;
import static rowdy.lang.RowdyGrammarConstants.CONSTANT;
import static rowdy.lang.RowdyGrammarConstants.FUNC_CALL;
import static rowdy.lang.RowdyGrammarConstants.ID;
import static rowdy.lang.RowdyGrammarConstants.THIS_REF;

/**
 *
 * @author Richard
 */
public class Atomic extends BaseNode {

  public Atomic(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  @Override
  public Value execute(Value leftValue) {
    BaseNode atomicType = getLeftMost();
    BaseNode child;
    Value value = new Value();
    switch(atomicType.symbol().id()) {
      case ATOMIC_ID:
        child = atomicType.get(ID);
        Value searchValue = new Value(child.symbol(), false);
        Node thisRef = atomicType.get(THIS_REF);
        if (thisRef.hasSymbols()) {
          value = instance.callStack.peek().getSymbolTable().getValue(searchValue);
          if (value == null) {
            throw new RuntimeException("The ID '" + searchValue + "' doesn't exist "
                  + "on line " + getLine());
          }
        } else {
          value = instance.fetch(searchValue, this);
        }
        break;
      case ATOMIC_CONST:
        child = atomicType.get(CONSTANT);
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
        child = atomicType.get(FUNC_CALL);
         {
          try {
            value = instance.executeFunc(child);
          } catch (ConstantReassignmentException ex) {
            Logger.getLogger(Atomic.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
        break;
    }
    return value;
  }
}
