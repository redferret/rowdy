
package rowdy;

import java.util.Arrays;

/**
 *
 * @author Richard
 */
public class Grammar {
  private final int id;
  private final int[] simpleProduction;

  public Grammar(int id, int[] simpleProduction) {
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
