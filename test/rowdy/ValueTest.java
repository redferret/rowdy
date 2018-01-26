/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rowdy;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Richard
 */
public class ValueTest {

  /**
   * Test of setValue method, of class Value.
   */
  @Test
  public void testSetValue() {
    Object value = "Some Value";
    Value instance = new Value();
    instance.setValue(value);
    assertNotNull(instance.getValue());
  }

  /**
   * Test of valueToString method, of class Value.
   */
  @Test
  public void testValueToString() {
    Value instance = new Value("\"Value\"");
    String expResult = "Value";
    String result = instance.valueToString();
    assertEquals(expResult, result);
  }

  /**
   * Test of valueToDouble method, of class Value.
   */
  @Test
  public void testValueToDouble() {
    Value instance = new Value("10.5");
    Double expResult = 10.5;
    Double result = instance.valueToDouble();
    assertEquals(expResult, result);
  }

  /**
   * Test of valueToSymbol method, of class Value.
   */
  @Test
  public void testValueToSymbol() {
    Value instance = new Value(new Symbol() {});
    Symbol result = instance.valueToSymbol();
    assertNotNull(result);
  }

  /**
   * Test of valueToBoolean method, of class Value.
   */
  @Test
  public void testValueToBoolean() {
    Value instance = new Value("true");
    Boolean expResult = true;
    Boolean result = instance.valueToBoolean();
    assertEquals(expResult, result);
    
    instance = new Value(false);
    expResult = false;
    result = instance.valueToBoolean();
    assertEquals(expResult, result);
  }

  /**
   * Test of getValue method, of class Value.
   */
  @Test
  public void testGetValue() {
    Value instance = new Value("String");
    Object result = instance.getValue();
    assertNotNull(result);
  }

  /**
   * Test of setAsConstant method, of class Value.
   */
  public void testIsConstant() {
    Value instance = new Value(0);
    instance.setAsConstant(true);
    assertTrue(instance.isConstant());
  }
  
}
