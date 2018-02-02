
package rowdy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import rowdy.exceptions.ParseException;
import static org.junit.Assert.*;
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
    try {
      lexer.parse("tokenizerTestFile1");
    } catch (FileNotFoundException | ParseException ex) {
      fail("Lexer failed to lex");
    }
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
