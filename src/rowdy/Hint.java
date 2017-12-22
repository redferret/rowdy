package rowdy;

/**
 * A hint holds a terminal and a hint value to aid the parser when the program
 * tree is being built. The hint is a production rule based on the current token
 * (terminal).
 *
 * @author Richard DeSilvey
 */
public class Hint {

  private final Terminal terminal;
  private final int hint;

  public Hint(Terminal terminal, int hint) {
    this.terminal = terminal;
    this.hint = hint;
  }

  public int getHint() {
    return hint;
  }

  public Terminal getTerminal() {
    return terminal;
  }
}
