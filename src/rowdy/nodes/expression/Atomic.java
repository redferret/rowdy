
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
    Value value = new Value();
    switch(atomicType.symbol().id()) {
      case ATOMIC_ID:
        child = (RowdyNode) atomicType.get(ID);
        value = new Value(child.symbol());
        break;
      case ATOMIC_CONST:
        child = (RowdyNode) atomicType.get(CONSTANT);
        value = new Value(((Terminal) child.symbol()).getName());
        break;
      case ATOMIC_FUNC_CALL:
        child = (RowdyNode) atomicType.get(FUNC_CALL);
        value = runner.executeFunc(child);
        break;
    }
    return value;
  }
}
