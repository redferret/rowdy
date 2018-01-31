package rowdy;

import java.util.Arrays;

/**
 * The translation from IDs to Symbols are held for the builder
 *
 * @author Richard DeSilvey
 */
public class ProductionSymbols {

  private Symbol[] defs;

  public ProductionSymbols() {
    defs = new Symbol[0];
  }

  public ProductionSymbols(Symbol[] defs) {
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
