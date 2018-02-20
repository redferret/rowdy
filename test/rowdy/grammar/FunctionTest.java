
package rowdy.grammar;

import growdy.Terminal;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import rowdy.Function;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import static org.junit.Assert.*;
/**
 *
 * @author Richard
 */
public class FunctionTest {
  
  public FunctionTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of getLineCalledOn method, of class Function.
   */
  @Test
  public void testGetLineCalledOn() {
    Function instance = new Function("Test Function", null, 10);
    int expResult = 10;
    int result = instance.getLineCalledOn();
    assertEquals(expResult, result);
  }

  /**
   * Test of setReturnValue method, of class Function.
   */
  @Test
  public void testGetAndSetReturnValue() {
    Value value = new Value("Return value", false);
    Function instance = new Function("Test Function", new HashMap<>(), 0);
    instance.setReturnValue(value);
    assertNotNull(instance.getReturnValue());
    assertEquals(value, instance.getReturnValue());
  }

  /**
   * Test of allocate method, of class Function.
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  @Test
  public void testAllocateString() throws ConstantReassignmentException {
    String idName = "someId";
    Value value = new Value(0, false);
    Function instance = new Function("Test Function", new HashMap<>(), 0);
    instance.getSymbolTable().allocate(idName, value, 0);
    Double expectedValue = 0d;
    Double actualValue = instance.getSymbolTable().getValue(idName).valueToDouble();
    assertEquals(expectedValue, actualValue);
  }

  /**
   * Test of allocate method, of class Function.
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  @Test
  public void testAllocateValue() throws ConstantReassignmentException {
    Terminal cur = new Terminal("ID", 0, "A");
    Value value = new Value(10, false);
    Function instance = new Function("Function", new HashMap<>(), 100);
    instance.getSymbolTable().allocate(cur, value, 0);
    Double expectedValue = 10d;
    Double actualValue = instance.getSymbolTable().getValue("A").valueToDouble();
    assertEquals(expectedValue, actualValue);
  }
  
  /**
   * Test of allocate method, of class Function.
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  @Test(expected = ConstantReassignmentException.class)
  public void testAllocateValueConstant() throws ConstantReassignmentException {
    Terminal cur = new Terminal("ID", 0, "A");
    Value value = new Value(10, true);
    Function instance = new Function("Function", new HashMap<>(), 100);
    instance.getSymbolTable().allocate(cur, value, 0);
    instance.getSymbolTable().allocate(cur, value, 0);
  }

  /**
   * Test of unset method, of class Function.
   * @throws rowdy.exceptions.ConstantReassignmentException
   */
  @Test
  public void testUnset() throws ConstantReassignmentException {
    String idName = "someId";
    Value value = new Value(0, false);
    Function instance = new Function("Test Function", new HashMap<>(), 0);
    instance.getSymbolTable().allocate(idName, value, 0);
    instance.getSymbolTable().unset(idName);
    assertNull(instance.getSymbolTable().getValue(idName));
  }

  /**
   * Test of getName method, of class Function.
   */
  @Test
  public void testGetName() {
    Function instance = new Function("Test Function", new HashMap<>(), 0);
    String expResult = "Test Function";
    String result = instance.getName();
    assertEquals(expResult, result);
  }

}
