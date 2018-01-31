
package rowdy;

import org.junit.Test;
import static rowdy.Rowdy.*;
import static org.junit.Assert.*;
/**
 *
 * @author Richard
 */
public class RowdyGrammarTest {
  
  private static final Integer EXPECTED_PRODUCTION_COUNT = 70;
  
  @Test
  public void testGrammarSize() {
    Integer actualCount = GRAMMAR.length;
    int diff = actualCount - EXPECTED_PRODUCTION_COUNT;
    assertEquals("Detected "+diff+" new grammar production rule(s)", 
            EXPECTED_PRODUCTION_COUNT, actualCount);
  }
  
}
