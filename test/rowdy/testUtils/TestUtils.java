/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rowdy.testUtils;

import rowdy.RowdyBuilder;
import rowdy.RowdyLexer;
import rowdy.Terminal;
import rowdy.Language;
import rowdy.Node;
import static rowdy.Rowdy.*;
import static org.junit.Assert.*;

/**
 *
 * @author Richard
 */
public class TestUtils {
  
  public static final RowdyLexer parser = 
          new RowdyLexer(TERMINALS, SPECIAL_SYMBOLS, ID, CONST);
  public static final Language rowdy = 
          Language.build(GRAMMAR, TERMINALS, NONTERMINALS);
  public static final RowdyBuilder builder = 
          RowdyBuilder.getBuilder(rowdy);
  
  public static Node getTestStatement(String testProgram, int stmtId) {
    Node root = getRoot(testProgram);
    
    Node statement = getFromAndTestNotNull(root, STATEMENT);
    return getFromAndTestNotNull(statement, stmtId);
  }
  
  public static Node getRoot(String testProgram) {
    parser.parseLine(testProgram);
    builder.buildAsSingleLine(parser);
    Node root = builder.getProgram();
    assertNotNull("Root Program is Null", root);
    return root;
  }
  
  public static Node getFromAndTestNotNull(Node from, int id) {
    return getFromAndTestNotNull(from, id, 0);
  }
  
  public static Node getFromAndTestNotNull(Node from, int id, int occur) {
    Node toFetch = from.get(id, occur, false);
    assertNotNull("Node doesn't contain the given id: " + id, toFetch);
    return toFetch;
  }
  
  public static void testForTerminal(Node terminal, String expected) {
    assertNotNull("Terminal Node is null", terminal);
    String actual = ((Terminal)terminal.symbol()).getName();
    assertEquals("Terminal's name doesn't match",expected, actual);
  }
  
  public static Node getAndTestSymbol(Node from, int nodeId, String expected){
    Node toFetch = getFromAndTestNotNull(from, nodeId);
    String actual = toFetch.symbol().getSymbolAsString();
    assertEquals("The expected Symbol is incorrect " + nodeId, expected, actual);
    return toFetch;
  }
  
  public static void testExpressionList(Node root) {
    Node exprList = getFromAndTestNotNull(root, EXPR_LIST);
    while(exprList.hasChildren()) {
      getFromAndTestNotNull(exprList, COMMA);
      getFromAndTestNotNull(exprList, EXPRESSION);
      exprList = getFromAndTestNotNull(exprList, EXPR_LIST);
    }
  }
}
