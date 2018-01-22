package rowdy;

import java.util.HashMap;

/**
 * Builds a generic language based on the list of values given. Each list
 * contains integer values that map to terminals and non-terminals that are
 * defined for the language. Indexing is important for each value. If a terminal
 * "IF" has an ID of 5 then it's location in all the other lists should be 5.
 * The language builds a grammar that hold production rules.
 *
 * @author Richard DeSilvey
 */
public class Language {

  private final HashMap<Integer, ProductionSymbols> GRAMMAR;
  private final HashMap<Integer, Symbol> SYMBOLS;

  /**
   * Produces a new language.
   *
   * @param terms The terminals
   * @param grammarRules The grammar production rules
   * @param nonterminals
   * @return A new language object.
   */
  public static Language build(ProductionRule[] grammarRules, String[] terms, 
          NonTerminal[] nonterminals) {
    return new Language(terms, grammarRules, nonterminals);
  }

  private Language(String[] terms, ProductionRule[] grammarRules, NonTerminal[] nonterminals) {
    GRAMMAR = new HashMap<>();
    SYMBOLS = new HashMap<>();
    for (int i = 0; i < terms.length; i++) {
      SYMBOLS.put(i, new Terminal(terms[i], i));
    }
    for (NonTerminal nonTerminal : nonterminals) {
      SYMBOLS.put(nonTerminal.id(), nonTerminal);
    }
    Symbol symbs[];
    for (ProductionRule productionRule : grammarRules) {
      int grammarId = productionRule.getId();
      int[] productionSymbols = productionRule.getProductionSymbols();
      symbs = new Symbol[productionSymbols.length];
      for (int j = 0; j < productionSymbols.length; j++) {
        int symbolID = productionSymbols[j];
        symbs[j] = SYMBOLS.get(symbolID);
      }
      GRAMMAR.put(grammarId, new ProductionSymbols(symbs));
    }
  }

  /**
   * Fetches a production rule from this language with a given production Hint.
   *
   * @param productionHint
   * @return
   */
  public ProductionSymbols getProductionSymbols(Hint productionHint) {
    return (productionHint == null) ? 
            new ProductionSymbols() 
            : 
            GRAMMAR.get(productionHint.getProductionHint());
  }

  /**
   * Fetches a language symbol with the given id.
   *
   * @param id The symbol's id
   * @return The non-terminal or terminal symbol.
   */
  public Symbol getSymbol(int id) {
    return SYMBOLS.get(id);
  }
}
