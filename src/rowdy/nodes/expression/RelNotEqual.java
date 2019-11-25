/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
import rowdy.RowdyObject;
import rowdy.Value;

/**
 *
 * @author Richard
 */
public class RelNotEqual extends BaseNode {
  
  public RelNotEqual(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
  }
  @Override
  public Object execute(Object leftValue) {
    BaseNode arithmExpr = getLeftMost();
    if (arithmExpr == null) {
      return instance.fetch((Value) leftValue, this);
    }
    leftValue = instance.fetch((Value) leftValue, this);
    Value rightValue = (Value) arithmExpr.execute(leftValue);
    Number left, right;
    if (((Value)leftValue).getValue() instanceof Boolean) {
      left = (boolean) ((Value)leftValue).getValue() ? 1 : 0;
    } else if (((Value)leftValue).getValue() instanceof String) {
      return new Value(true, false);
    } else if (((Value)leftValue).getValue() instanceof RowdyObject){
      return new Value(true, false);
    } else {
      left = (Number) ((Value)leftValue).getValue();
    }
    if (rightValue.getValue() instanceof Boolean) {
      right = (boolean) rightValue.getValue() ? 1 : 0;
    } else if (rightValue.getValue() instanceof String) {
      return new Value(true, false);
    } else if (((Value)rightValue).getValue() instanceof RowdyObject){
      return new Value(true, false);
    } else {
      right = (Number) rightValue.getValue();
    }
    
    if (left instanceof Double && right instanceof Integer) {
      right = (double) ((Integer)right + 0.0d);
    } else if (right instanceof Double && left instanceof Integer) {
      left = (double) ((Integer)left + 0.0d);
    }
    
    if (left instanceof Integer && right instanceof Long) {
      left = (long) ((Integer)left + 0L);
    } else if (right instanceof Integer && left instanceof Long) {
      right = (long) ((Integer)right + 0L);
    }
    
    if (left == null && right == null) {
      return new Value(false, false);
    } else if (left == null && right != null || left != null && right == null) {
      return new Value(true, false);
    }
    
    return new Value(!left.equals(right), false);
  }
}
