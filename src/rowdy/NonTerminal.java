package rowdy;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Richard DeSilvey
 */
public class NonTerminal extends Symbol {

  private ArrayList<Hint> hints;

  public NonTerminal(String symbol, int id) {
    super(symbol, id);
  }

  public void setHints(Hint[] hints) {
    this.hints = new ArrayList<>(Arrays.asList(hints));
  }

  public Hint getHint(int id) {
    if (hints == null) {
      return null;
    }
    for (int i = 0; i < hints.size(); i++) {
      if (hints.get(i).getTerminal() != null) {
        if (hints.get(i).getTerminal().id() == id) {
          return hints.get(i);
        }
      }
    }
    return null;
  }
}
