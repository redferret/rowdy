/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rowdy.nodes;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Richard
 */
public class RowdyNodeFactoryTest extends TestCase {
  
  public RowdyNodeFactoryTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(RowdyNodeFactoryTest.class);
    return suite;
  }

  public void testGetNode() {
  }
  
}
