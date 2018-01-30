package rowdy;

/**
 * A symbol is a terminal or non-terminal defined by a language definition.
 *
 * @author Richard DeSilvey
 */
public abstract class Symbol {

  protected String symbol;
  protected int id;

  public Symbol() {
    symbol = "";
    id = -1;
  }

  public Symbol(String symbol, int id) {
    this.symbol = symbol;
    this.id = id;
  }

  public Symbol(Token token) {
    symbol = token.getSymbol();
    id = token.getID();
  }

  public int id() {
    return id;
  }

  public String getSymbolAsString() {
    return symbol;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public String toString() {
    return "'"+symbol+": " + id+"' ";
  }
}
