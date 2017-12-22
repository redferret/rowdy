package rowdy;
import java.util.HashMap;
/**
 * Builds a generic language based on the list of values given.
 * Each list contains integer values that map to terminals and non-terminals
 * that are defined for the language. Indexing is important for each value.
 * If a terminal "IF" has an ID of 5 then it's location in all the other
 * lists should be 5. The language builds a grammar that hold production rules.
 * 
 * @author Richard DeSilvey
 */
public class Language {
    private final HashMap<Integer, Rule> GRAMMAR;
    private final HashMap<Integer, Symbol> SYMBOLS;
    /**
     * Produces a new language.
     * @param terms The terminals
     * @param nTerms The non-terminals
     * @param grammarRules The grammar production rules
     * @param grammarHints The grammar's recursive-decent parsing hints
     * @return A new language object.
     */
    public static Language build(String[] terms, String[] nTerms, 
            int[][] grammarRules, int[][][] grammarHints){
        return new Language(terms, nTerms, grammarRules, grammarHints);
    }
    private Language(String[] terms, String[] nTerms, int[][] grammarRules, 
                                            int[][][] grammarHints){
        GRAMMAR = new HashMap<>();
        SYMBOLS = new HashMap<>();
        for (int i = 0; i < terms.length; i++){
            SYMBOLS.put(i, new Terminal(terms[i], i));
        }
        final int TERMINAL = 0, RULE = 1;
        Hint[] hints;
        for (int i = 0; i < nTerms.length; i++){
            NonTerminal nt = new NonTerminal(nTerms[i], i + terms.length);
            Terminal terminal;
            hints = new Hint[grammarHints[i].length];
            for (int h = 0; h < grammarHints[i].length; h++){
                int tid = grammarHints[i][h][TERMINAL];
                terminal = (Terminal) SYMBOLS.get(tid);
                hints[h] = new Hint(terminal, grammarHints[i][h][RULE]);
            }
            nt.setHints(hints);
            SYMBOLS.put(i + terms.length, nt);
        }
        Symbol symbs[];
        for (int i = 0; i < grammarRules.length; i++){
            symbs = new Symbol[grammarRules[i].length];
            for (int j = 0; j < grammarRules[i].length; j++){
                int symbolID = grammarRules[i][j];
                symbs[j] = SYMBOLS.get(symbolID);
            }
            GRAMMAR.put(i, new Rule(symbs));
        }
    }
    /**
     * Fetches a production rule from this language with a given production
     * Hint.
     * @param productionHint
     * @return 
     */
    public Rule getProductionRule(Hint productionHint){
        if (productionHint == null){
            return new Rule();
        }
        return (productionHint == null) ? new Rule() : GRAMMAR.get(productionHint.getHint());
    }
    /**
     * Fetches a language symbol with the given id.
     * @param id The symbol's id
     * @return The non-terminal or terminal symbol.
     */
    public Symbol getSymbol(int id){
        return SYMBOLS.get(id);
    }
}
