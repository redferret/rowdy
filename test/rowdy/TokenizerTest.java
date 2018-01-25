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
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Richard
 */
public class TokenizerTest {
   
  private Tokenizer tokenizer;
  private final String[] reserved = {"add", "+", "-"};
  private final String operators = "+ -";
  
  public TokenizerTest() {
  }
  
  @Before
  public void setUp() throws IOException {
    File file = new File("tokenizerTestFile1");
    BufferedWriter bf = new BufferedWriter(new FileWriter(file));
    bf.write("add a + 25 - 1");
    bf.close();
    tokenizer = new Tokenizer(reserved, operators, 3, 4);
    tokenizer.parse("tokenizerTestFile1");
    file.delete();
  }

  @After
  public void tearDown() {
  }

  @Test
  public void tokenCountTest() {
    Integer numberOfTokens = tokenizer.tokenCount();
    Integer expectedCount = 7;
    assertEquals("The number of Tokens is incorrect", expectedCount, numberOfTokens);
  }

  /**
   * Test of hasToken method, of class Tokenizer.
   */
  @Test
  public void testHasToken() {
    Boolean hasToken = tokenizer.hasToken();
    assertTrue("The number of Tokens is incorrect", hasToken);
  }

  /**
   * Test of getToken method, of class Tokenizer.
   */
  @Test
  public void testGetToken() {
    Token token;
    Integer[] expectedIds = {0, 3, 1, 4, 2, 4, 200};
    for (Integer expectedId : expectedIds){
      token = tokenizer.getToken();
      Integer tokenId = token.getID();
      assertEquals("Token id mismatch", expectedId, tokenId);
    }

  }
  
}
