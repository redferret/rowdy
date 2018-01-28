
package rowdy;

import java.util.List;
import static rowdy.Rowdy.*;

/**
 *
 * @author Richard
 */
public class RowdyBuilder {
  
  private int line;
  private RowdyLexer parser;
  private Token currentToken;
  private final Language language;
  private Node root;
  
  private RowdyBuilder(Language language) {
    line = 0;
    this.language = language;
    
  }
  
  public static RowdyBuilder getBuilder(Language language){
    return new RowdyBuilder(language);
  }
  
  public Node getProgram() {
    return root;
  }
  
  /**
   * Builds the parse tree with the given program file and language definitions.
   * @param parser
   */
  public void build(RowdyLexer parser) {
    this.buildAs(parser, PROGRAM);
  }

  public void buildAsSingleLine(RowdyLexer parser) {
    this.buildAs(parser, STMT_LIST);
  }
  
  private void buildAs(RowdyLexer parser, int programType) {
    this.parser = parser;
    NonTerminal program = (NonTerminal) language.getSymbol(programType);
    root = new Node(program, 0);
    currentToken = this.parser.getToken();
    if (currentToken == null){
      return;
    }
    consumeEOLN();
    int id = currentToken.getID();
    addToNode(root, produce(program, id));
    build(root);
  }
  
  private void consumeEOLN() {
    while (currentToken.getID() == EOLN) {
      if (currentToken.getID() == EOLN) {
        line++;
      }
      currentToken = this.parser.getToken();
      if (currentToken == null){
        return;
      }
    }
  }

  /**
   * Walks through the tree recursively building on non-terminals. If a syntax
   * error is detected the line number is printed out.
   *
   * @param parent
   */
  public void build(Node parent) {
    Symbol symbol;
    ProductionSymbols rule;
    List<Node> children = parent.getAll();
    Node current;
    for (int i = 0; i < children.size(); i++) {
      current = children.get(i);
      symbol = current.symbol();
      if (symbol instanceof NonTerminal) {
        if (currentToken == null) break;
        rule = produce((NonTerminal) symbol, currentToken.getID());
        addToNode(current, rule);
        build(current);
      } else {
        if (symbol.id() != currentToken.getID()) {
          throw new RuntimeException("Syntax error, unexpected token '"
                  + currentToken.getSymbol() + "' on Line " + line);
        }
        children.remove(i);
        Terminal terminal = new Terminal(symbol.getSymbol(), currentToken.getID(), currentToken.getSymbol());
        children.add(i, new Node(terminal, line));
        currentToken = parser.getToken();
        while (currentToken != null && currentToken.getID() == EOLN) {
          line++;
          currentToken = parser.getToken();
        }
        if (currentToken == null) break;
      }
    }
  }

  /**
   * Builds a rule from the given NonTerminal using the id to map onto a hint.
   *
   * @param symbol The NonTerminal for reference
   * @param terminal The id from a token, usually a terminal
   * @return Fetches a production rule from the language's grammar.
   */
  public ProductionSymbols produce(NonTerminal symbol, int terminal) {
    Hint productionHint = symbol.getHint(terminal);
    return language.getProductionSymbols(productionHint);
  }

  /**
   * Adds to the parent node the production rules. Each child is from the
   * production rule.
   *
   * @param parent The parent being added to
   * @param rule The production rule.
   */
  public void addToNode(Node parent, ProductionSymbols rule) {
    Symbol[] symbols = rule.getSymbols();
    for (Symbol symbol : symbols) {
      Node node = new Node(symbol, line);
      parent.add(node);
    }
  }
}
