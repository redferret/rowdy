/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rowdy;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
    String[] terms = {"add"};
    NonTerminal[] nonterminals = {
      new NonTerminal("prog", PROGRAM, 
        new int[][]{{0, PRULE_PROGRAM}}),
      new NonTerminal("statement", STATEMENT, 
            new int[][]{{0, PRULE_STATEMENT}}),
    };
    
  private Language language;
  
  public LanguageTest() {
    language = Language.build(grammarRules, terms, nonterminals);
  }

  /**
   * Test of build method, of class Language.
   */
  @Test
  public void testBuild() {
    assertNotNull(language);
  }

  /**
   * Test of getProductionSymbols method, of class Language.
   */
  @Test
  public void testGetProductionSymbols() {
    Hint productionHint = new Hint(0, PRULE_STATEMENT);
    ProductionSymbols result = language.getProductionSymbols(productionHint);
    assertNotNull(result);
    String nonTerminalName = "statement";
    String actualName = ((NonTerminal)result.getSymbols()[0]).getSymbol();
    assertEquals(nonTerminalName, actualName);
  }

  /**
   * Test of getSymbol method, of class Language.
   */
  @Test
  public void testGetSymbol() {
    int id = 0;
    String expResult = "add";
    Terminal symbol = (Terminal)language.getSymbol(id);
    String symbolName = symbol.getSymbol();
    assertEquals(expResult, symbolName);
  }
  
}
