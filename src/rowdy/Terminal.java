package rowdy;

/**
 * Terminals are IDs, constants, keywords, etc. for a programming language
 * and is defined as a symbol.
 * The real value of an ID is the name of the identifier which is used to
 * search for it's value in the symbol table.
 * The real value of an 'if' is "if"
 * @author Richard DeSilvey
 */
public class Terminal extends Symbol {

  private String realValue;
  private Object boundTo;

  public Terminal(String symbol, int id) {
    super(symbol, id);
    set("");
  }

  public Terminal(String symbol, int id, String value) {
    super(symbol, id);
    set(value);
  }

  private void set(String value) {
    realValue = value;
    boundTo = null;
  }

  public void bindTo(Object bindee) {
    boundTo = bindee;
  }

  public void setValue(String value) {
    realValue = value;
  }

  public String getName() {
    return realValue;
  }

  public Object getBinding() {
    return boundTo;
  }

  @Override
  public String toString() {
    return realValue;
  }
}
