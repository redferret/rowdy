package rowdy;

import java.util.Arrays;

/**
 * A production rule holds all the terminals and non-terminals for building a
 * parse tree.
 *
 * @author Richard DeSilvey
 */
public class Rule {

  private Symbol[] defs;

  public Rule() {
    defs = new Symbol[0];
  }

  public Rule(Symbol[] defs) {
    this.defs = new Symbol[defs.length];
    System.arraycopy(defs, 0, this.defs, 0, defs.length);
  }

  public Symbol[] getSymbols() {
    return defs;
  }

  public String toString() {
    return Arrays.toString(defs);
  }
}
