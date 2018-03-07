/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rowdy.nodes.expression;

import growdy.Symbol;
import rowdy.BaseNode;
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
  public Value execute(Value leftValue) {
    BaseNode arithmExpr = getLeftMost();
    if (arithmExpr == null) {
      return instance.fetch(leftValue, this);
    }
    leftValue = instance.fetch(leftValue, this);
    Value rightValue = arithmExpr.execute(leftValue);
    double left, right;
    if (leftValue.getValue() instanceof Boolean) {
      left = leftValue.valueToBoolean() ? 1 : 0;
    } else {
      left = leftValue.valueToDouble();
    }
    if (rightValue.getValue() instanceof Boolean) {
      right = rightValue.valueToBoolean() ? 1 : 0;
    } else {
      right = rightValue.valueToDouble();
    }
    return new Value(left != right, false);
  }
}