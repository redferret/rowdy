
package rowdy;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Richard
 */
public class LanguageTest {
  
  private static final int PROGRAM = 100, STATEMENT = 101;
  private static final int PRULE_PROGRAM = 0, PRULE_STATEMENT = 1;
  
  private static final ProductionRule[] grammarRules = {
      new ProductionRule(PRULE_PROGRAM,
          new int[]{PROGRAM}),
      new ProductionRule(PRULE_STATEMENT,
          new int[]{STATEMENT})
  };
  
  private static final String[] terms = {"add"};
  private static final NonTerminal[] nonterminals = {
      new NonTerminal("prog", PROGRAM, 
        new int[][]{{0, PRULE_PROGRAM}}),
      new NonTerminal("statement", STATEMENT, 
            new int[][]{{0, PRULE_STATEMENT}}),
  };

  /**
   * Test of getProductionSymbols method, of class Language.
   */
  @Test
  public void testGetProductionSymbols() {
    Language language = getSimpleTestLanguage();
    Hint productionHint = new Hint(0, PRULE_STATEMENT);
    ProductionSymbols result = language.getProductionSymbols(productionHint);
    assertNotNull(result);
    String nonTerminalName = "statement";
    String actualName = ((NonTerminal)result.getSymbols()[0]).getSymbolAsString();
    assertEquals(nonTerminalName, actualName);
  }

  /**
   * Test of getSymbolAsString method, of class Language.
   */
  @Test
  public void testGetSymbol() {
    Language language = getSimpleTestLanguage();
    int id = 0;
    String expResult = "add";
    Terminal symbol = (Terminal)language.getSymbol(id);
    String symbolName = symbol.getSymbolAsString();
    assertEquals(expResult, symbolName);
  }
  
  private Language getSimpleTestLanguage() {
    Language language = Language.build(grammarRules, terms, nonterminals);
    assertNotNull(language);
    return language;
  }
  
}
