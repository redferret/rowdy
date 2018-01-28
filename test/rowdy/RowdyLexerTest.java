/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rowdy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Richard
 */
public class RowdyLexerTest {
   
  private RowdyLexer lexer;
  private final String[] reserved = {"add", "+", "-"};
  private final String operators = "+ -";
  
  public RowdyLexerTest() {
  }
  
  @Before
  public void setUp() throws IOException {
    File file = new File("tokenizerTestFile1");
    BufferedWriter bf = new BufferedWriter(new FileWriter(file));
    bf.write("add a + 25 - 1");
    bf.close();
    lexer = new RowdyLexer(reserved, operators, 3, 4);
    lexer.parse("tokenizerTestFile1");
    file.delete();
  }

  @After
  public void tearDown() {
  }

  @Test
  public void tokenCountTest() {
    Integer numberOfTokens = lexer.tokenCount();
    Integer expectedCount = 7;
    assertEquals("The number of Tokens is incorrect", expectedCount, numberOfTokens);
  }

  /**
   * Test of hasToken method, of class RowdyLexer.
   */
  @Test
  public void testHasToken() {
    Boolean hasToken = lexer.hasToken();
    assertTrue("The number of Tokens is incorrect", hasToken);
  }

  /**
   * Test of getToken method, of class RowdyLexer.
   */
  @Test
  public void testGetToken() {
    Token token;
    Integer[] expectedIds = {0, 3, 1, 4, 2, 4, 200};
    for (Integer expectedId : expectedIds){
      token = lexer.getToken();
      Integer tokenId = token.getID();
      assertEquals("Token id mismatch", expectedId, tokenId);
    }

  }

  /**
   * Test of parse method, of class RowdyLexer.
   * @throws java.lang.Exception
   */
  public void testParse() throws Exception {
  }

  /**
   * Test of parseLine method, of class RowdyLexer.
   */
  public void testParseLine() {
  }

  /**
   * Test of parseCode method, of class RowdyLexer.
   */
  public void testParseCode() {
  }

  /**
   * Test of tokenCount method, of class RowdyLexer.
   */
  public void testTokenCount() {
  }
  
}
