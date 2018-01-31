package rowdy;

/**
 * A Token is an incomplete symbol but has an ID matched with a string that
 * is the source code pulled in by a lexer.
 *
 * @author Richard DeSilvey
 */
public class Token {

  private final int token;
  private final String symbol;

  public Token(int token, String symbol) {
    this.token = token;
    this.symbol = symbol;
  }

  public int getID() {
    return token;
  }

  public String getSymbol() {
    return symbol;
  }

  @Override
  public String toString() {
    return symbol + " " + token;
  }
}
