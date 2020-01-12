
package rowdy.testutils;

import growdy.Grammar;
import growdy.GRowdy;
import growdy.NonTerminal;
import growdy.Symbol;
import growdy.Terminal;
import growdy.exceptions.AmbiguousGrammarException;
import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;
import java.util.HashMap;
import rowdy.RowdyInstance;
import rowdy.nodes.RowdyNode;
import rowdy.nodes.RowdyNodeFactory;
import rowdy.BaseNode;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;
import java.util.List;

import static rowdy.testlang.lang.RowdyGrammarConstants.ID;
import static rowdy.Rowdy.getBuilder;
import static org.junit.Assert.fail;
import rowdy.Function;
/**
 *
 * @author Richard
 */
public class TestUtils {
  
  public final static RowdyInstance rowdyInstance;
  
  static {
    rowdyInstance = new RowdyInstance();
    try {
      rowdyInstance.declareSystemConstants();
    } catch (ConstantReassignmentException ex) {
      fail("Unable to declare system constants");
    }
    RowdyNode.initRunner(rowdyInstance);
    rowdyInstance.callStack.push(new Function("testShell", new HashMap<>(), 0));
  }
  
  public static void trimEmptyChildren(BaseNode root) {
    List<BaseNode> children = root.getAll();
    for (int i = 0; i < children.size(); i++) {
      Symbol symbol = children.get(i).symbol();
      if (symbol instanceof NonTerminal){
        if (children.get(i).isTrimmable() && !children.get(i).hasSymbols()){
          children.remove(i--);
        }
      }
    }
  }
  
  public static boolean isset(String idName) {
    return rowdyInstance.isset(new Value(new Terminal("id", ID, idName), false));
  }
  
  public static Value fetch(String idName) {
    return rowdyInstance.fetch(new Value(new Terminal("id", ID, idName), false), null);
  }
  
  public static BaseNode getTestStatement(String sourceCode, int programNode) {
    Grammar grBuilder = getBuilder();
    RowdyNodeFactory factory = new RowdyNodeFactory();
    GRowdy growdy = GRowdy.getInstance(grBuilder, factory);
    try {
      growdy.buildFromString(sourceCode, programNode);
    } catch (ParseException | SyntaxException | AmbiguousGrammarException ex) {
      fail("Build error: " + ex.getLocalizedMessage());
    }
    BaseNode root = (BaseNode) growdy.getProgram();
    try {
      rowdyInstance.optimizeProgram(root);
    } catch (ConstantReassignmentException ex) {
      fail("Run error: " + ex.getLocalizedMessage());
    }
    return root;
  }
  
}
