
package rowdy.testutils;

import growdy.GRBuilder;
import growdy.GRowdy;
import growdy.Node;
import growdy.exceptions.AmbiguousGrammarException;
import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;
import static org.junit.Assert.fail;
import static rowdy.Rowdy.getBuilder;

/**
 *
 * @author Richard
 */
public class TestUtils {
  
  public static Node getTestStatement(String sourceCode, int programNode) {
    GRBuilder grBuilder = getBuilder();
    GRowdy growdy = GRowdy.getInstance(grBuilder);
    try {
      growdy.buildFromString(sourceCode, programNode);
    } catch (ParseException | SyntaxException | AmbiguousGrammarException ex) {
      fail("Unable to load or build grammar " + ex.getLocalizedMessage());
    }
    return growdy.getProgram();
  }
  
}
