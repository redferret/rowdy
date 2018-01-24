
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import rowdy.Tokenizer;

public class TokenizerTest {
  
  private Tokenizer tokenizer;
  private final String[] reservedWords = {"add"};
  private final String operators = "+ -";
  
  @Before
  public void setUp() {
    tokenizer = new Tokenizer(reservedWords, operators, 0, 1);
  }

  @After
  public void tearDown() {
  }

  @Test
  public void tokenCountTest() {
    tokenizer.parse("tokenizerTestFile1");
    Integer numberOfTokens = tokenizer.tokenCount();
    Integer expectedCount = 6;
    assertEquals("Number of Tokens not correct", expectedCount, numberOfTokens);
  }
}
