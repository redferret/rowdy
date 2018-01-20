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

  private final HashMap<Integer, Rule> GRAMMAR;
  private final HashMap<Integer, Symbol> SYMBOLS;

  /**
   * Produces a new language.
   *
   * @param terms The terminals
   * @param grammarRules The grammar production rules
   * @param nonterminals
   * @return A new language object.
   */
  public static Language build(String[] terms, int[][] grammarRules, NonTerminal[] nonterminals) {
    return new Language(terms, grammarRules, nonterminals);
  }

  private Language(String[] terms, int[][] grammarRules, NonTerminal[] nonterminals) {
    GRAMMAR = new HashMap<>();
    SYMBOLS = new HashMap<>();
    for (int i = 0; i < terms.length; i++) {
      SYMBOLS.put(i, new Terminal(terms[i], i));
    }
    for (NonTerminal nonTerminal : nonterminals) {
      SYMBOLS.put(nonTerminal.id(), nonTerminal);
    }
    Symbol symbs[];
    for (int i = 0; i < grammarRules.length; i++) {
      symbs = new Symbol[grammarRules[i].length];
      for (int j = 0; j < grammarRules[i].length; j++) {
        int symbolID = grammarRules[i][j];
        symbs[j] = SYMBOLS.get(symbolID);
      }
      GRAMMAR.put(i, new Rule(symbs));
    }
  }

  /**
   * Fetches a production rule from this language with a given production Hint.
   *
   * @param productionHint
   * @return
   */
  public Rule getProductionRule(Hint productionHint) {
    return (productionHint == null) ? 
            new Rule() 
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
