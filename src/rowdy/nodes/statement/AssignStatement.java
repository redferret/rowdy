
package rowdy.nodes.statement;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.Calculator;
import static rowdy.RowdyInstance.ATOMIC_GET;
import rowdy.Value;
import rowdy.nodes.RowdyNode;
import rowdy.exceptions.ConstantReassignmentException;

import static rowdy.lang.RowdyGrammarConstants.ASSIGN_VALUE;
import static rowdy.lang.RowdyGrammarConstants.BECOMES_EXPR;
import static rowdy.lang.RowdyGrammarConstants.CONST_OPT;
import static rowdy.lang.RowdyGrammarConstants.GLOBAL_DEF;
import static rowdy.lang.RowdyGrammarConstants.ID_MODIFIER;
import static rowdy.lang.RowdyGrammarConstants.ID;
import static rowdy.RowdyInstance.ATOMIC_SET;
import static rowdy.lang.RowdyGrammarConstants.COMP_ADD_;
import static rowdy.lang.RowdyGrammarConstants.COMP_DIV_;
import static rowdy.lang.RowdyGrammarConstants.COMP_MUL_;
import static rowdy.lang.RowdyGrammarConstants.COMP_SUB_;
import static rowdy.lang.RowdyGrammarConstants.DECREMENT_EXPR;
import static rowdy.lang.RowdyGrammarConstants.ID_;
import static rowdy.lang.RowdyGrammarConstants.INCREMENT_EXPR;
import static rowdy.lang.RowdyGrammarConstants.REF_ACCESS;

/**
 *
 * @author Richard
 */
public class AssignStatement extends BaseNode {
  
  public AssignStatement(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  
  @Override
  public Object execute(Object leftValue) {
    BaseNode assignValueNode = get(ASSIGN_VALUE);
    Value assignValue = new Value();
    BaseNode assignType = assignValueNode.getLeftMost();
    switch(assignType.symbol().id()) {
      case BECOMES_EXPR:
        RowdyNode becomesExpr = (RowdyNode) assignValueNode.getLeftMost();
        Value v = (Value) becomesExpr.execute();
        Object val = v.getValue();
        assignValue.setValue(val);
        break;
      case INCREMENT_EXPR:
        instance.RAMAccess(this, assignValue, ATOMIC_GET);
        assignValue = Calculator.calculate(assignValue, new Value(1), null, Calculator.Operation.ADD);
        break;
      case DECREMENT_EXPR:
        instance.RAMAccess(this, assignValue, ATOMIC_GET);
        assignValue = Calculator.calculate(assignValue, new Value(-1), null, Calculator.Operation.ADD);
        break;
      case COMP_ADD_:
        instance.RAMAccess(this, assignValue, ATOMIC_GET);
        BaseNode expr = assignType.getLeftMost();
        Value rightHandValue = (Value) expr.execute();
        assignValue = Calculator.calculate(assignValue, rightHandValue, null, Calculator.Operation.ADD);
        break;
      case COMP_SUB_:
        instance.RAMAccess(this, assignValue, ATOMIC_GET);
        expr = assignType.getLeftMost();
        rightHandValue = (Value) expr.execute();
        assignValue = Calculator.calculate(assignValue, rightHandValue, null, Calculator.Operation.SUBTRACT);
        break;
      case COMP_MUL_:
        instance.RAMAccess(this, assignValue, ATOMIC_GET);
        expr = assignType.getLeftMost();
        rightHandValue = (Value) expr.execute();
        assignValue = Calculator.calculate(assignValue, rightHandValue, null, Calculator.Operation.MULTIPLY);
        break;
      case COMP_DIV_:
        instance.RAMAccess(this, assignValue, ATOMIC_GET);
        expr = assignType.getLeftMost();
        rightHandValue = (Value) expr.execute();
        assignValue = Calculator.calculate(assignValue, rightHandValue, null, Calculator.Operation.DIVIDE);
        break;
    }

    if (assignValue == null) {
      return null;
    }

    RowdyNode idModifier = (RowdyNode) get(ID_MODIFIER);
    if (idModifier != null && idModifier.hasSymbols()) {
      switch (idModifier.getLeftMost().symbol().id()) {
        case CONST_OPT:
          assignValue.setAsConstant(true);
          break;
        case GLOBAL_DEF:
          BaseNode idNode = get(REF_ACCESS).get(ID_).get(ID);
          try {
            instance.setAsGlobal(idNode.symbol().toString(), assignValue);
          } catch (ConstantReassignmentException ex) {
            throw new RuntimeException(ex);
          }
          return null;
      }
    } else {
      assignValue.setAsConstant(false);
    }
    
    instance.RAMAccess(this, assignValue, ATOMIC_SET);
    
    return null;
  }

}