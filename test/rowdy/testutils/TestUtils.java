
package rowdy.testutils;

import growdy.GRBuilder;
import growdy.GRowdy;
import growdy.Node;
import growdy.NonTerminal;
import growdy.Symbol;
import growdy.exceptions.AmbiguousGrammarException;
import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;
import java.util.List;
import rowdy.RowdyInstance;
import rowdy.nodes.RowdyNode;
import rowdy.nodes.RowdyNodeFactory;
import static org.junit.Assert.fail;
import static rowdy.Rowdy.getBuilder;


/**
 *
 * @author Richard
 */
public class TestUtils {
  
  public static RowdyInstance rowdyInstance = new RowdyInstance();
  
  public static void trimEmptyChildren(RowdyNode root) {
    List<Node> children = root.getAll();
    for (int i = 0; i < children.size(); i++) {
      Symbol symbol = children.get(i).symbol();
      if (symbol instanceof NonTerminal){
        if (children.get(i).isTrimmable() && !children.get(i).hasSymbols()){
          children.remove(i--);
        }
      }
    }
  }
  
  public static RowdyNode getTestStatement(String sourceCode, int programNode) {
    RowdyNode.initRunner(rowdyInstance);
    GRBuilder grBuilder = getBuilder();
    RowdyNodeFactory factory = new RowdyNodeFactory();
    GRowdy growdy = GRowdy.getInstance(grBuilder, factory);
    try {
      growdy.buildFromString(sourceCode, programNode);
    } catch (ParseException | SyntaxException | AmbiguousGrammarException ex) {
      fail("Unable to load or build grammar " + ex.getLocalizedMessage());
    }
    return (RowdyNode) growdy.getProgram();
  }
  
}
