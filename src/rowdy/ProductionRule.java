
package rowdy;

import java.util.Arrays;

/**
 * A piece to a grammar that contains ids for a builder to consume and use to
 * build a parse tree.
 * @author Richard DeSilvey
 */
public class ProductionRule {
  private final int id;
  private final int[] simpleProduction;

  public ProductionRule(int id, int[] simpleProduction) {
    this.id = id;
    this.simpleProduction = simpleProduction;
  }

  public int getId() {
    return id;
  }

  public int[] getProductionSymbols() {
    return simpleProduction;
  }
 
  @Override
  public String toString() {
    return id + ": " + Arrays.toString(simpleProduction);
  }
}
