/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rowdy;

import growdy.Terminal;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.testlang.lang.RowdyGrammarConstants.ID;

/**
 *
 * @author Richard
 */
public class SymbolTableTest extends TestCase {
  
  public SymbolTableTest(String testName) {
    super(testName);
  }

  public void testAllocateWithStringName() throws ConstantReassignmentException {
    SymbolTable table = new SymbolTable(new HashMap<>(), null);
    table.allocate("Test", new Value(100, false), 0, true);
    assertNotNull(table.getValue("Test"));
    Integer expected = 100;
    Integer actual = (Integer) table.getValue("Test").getValue();
    assertEquals(expected, actual);
  }
  
  public void testAllocateWithTerminal() throws ConstantReassignmentException {
    SymbolTable table = new SymbolTable(new HashMap<>(), null);
    table.allocate(new Terminal("id", ID, "var1"), new Value(100, false), 0);
    assertNotNull(table.getValue("var1"));
    Integer expected = 100;
    Integer actual = (Integer) table.getValue("var1").getValue();
    assertEquals(expected, actual);
  }
  
  public void testConstAllocate(){
    SymbolTable table = new SymbolTable(new HashMap<>(), null);
    try {
      table.allocate("var1", new Value(100, true), 0, true);
      table.allocate("var1", new Value(100, false), 0, true);
      fail("Expected ConstantReassignmentException to be thrown");
    } catch (ConstantReassignmentException ex) {}
    
  }
  
  public void testAllocateWithHashMap() {
    SymbolTable table = new SymbolTable(new HashMap<>(), null);
    HashMap<String, Value> testMap = new HashMap<>();
    testMap.put("var1", new Value(100, false));
    testMap.put("var2", new Value(200, false));
    testMap.put("var3", new Value(300, false));
    table.allocate(testMap);
    assertNotNull(table.getValue("var1"));
    assertNotNull(table.getValue("var2"));
    assertNotNull(table.getValue("var3"));
  }
  
  public void testInitWithHashMap() {
    HashMap<String, Value> testMap = new HashMap<>();
    testMap.put("var1", new Value(100, false));
    testMap.put("var2", new Value(200, false));
    testMap.put("var3", new Value(300, false));
    SymbolTable table = new SymbolTable(testMap, null);
    assertNotNull(table.getValue("var1"));
    assertNotNull(table.getValue("var2"));
    assertNotNull(table.getValue("var3"));
  }
  
  public void testGetValueWithNull() {
    SymbolTable table = new SymbolTable(new HashMap<>(), null);
    assertNull(table.getValue((Value) null));
  }
  
  public void testFree() {
    SymbolTable table = new SymbolTable(new HashMap<>(), null);
    HashMap<String, Value> testMap = new HashMap<>();
    testMap.put("var1", new Value(100, false));
    testMap.put("var2", new Value(200, false));
    testMap.put("var3", new Value(300, false));
    table.allocate(testMap);
    table.free();
    assertNull(table.getValue("var1"));
    assertNull(table.getValue("var2"));
    assertNull(table.getValue("var3"));
  }
  
}
