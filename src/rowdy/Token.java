package rowdy;

/**
 * Wrapper class that holds a symbol and an id.
 *
 * @author Richard DeSilvey
 */
public class Token {

  private int token;
  private String symbol;

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
