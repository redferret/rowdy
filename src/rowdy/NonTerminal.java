package rowdy;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Richard DeSilvey
 */
public class NonTerminal extends Symbol {

  private List<Hint> hints;

  public NonTerminal(String symbol, int id, int[][] hints) {
    super(symbol, id);
    this.hints = new ArrayList<>();
    final int TERMINAL = 0, PRODUCTION_RULE = 1;
    for (int[] hint : hints) {
      Hint h = new Hint(hint[TERMINAL], hint[PRODUCTION_RULE]);
      this.hints.add(h);
    }
  }

  public Hint getHint(int id) {
    if (hints == null) {
      return null;
    }
    for (int i = 0; i < hints.size(); i++) {
      if (hints.get(i).getTerminalId() == id) {
        return hints.get(i);
      }
    }
    return null;
  }
}
