package rowdy;

import java.util.ArrayList;

/**
 * Tree Node for the parse tree
 */
public class Node {

  private final ArrayList<Node> children;
  private final Symbol def;
  private Boolean seqActive;
  private final int line;

  public Node(Symbol def, int lineNumber) {
    children = new ArrayList<>();
    this.def = def;
    seqActive = false;
    this.line = lineNumber;
  }

  public Boolean isSeqActive() {
    return seqActive;
  }

  public void setSeqActive(Boolean seqActive) {
    this.seqActive = seqActive;
  }
  
  public int getLine() {
    return line;
  }

  public void add(Node child) {
    children.add(child);
  }

  public Symbol symbol() {
    return def;
  }

  public boolean hasChildren() {
    return !children.isEmpty();
  }

  public Node getLeftMostChild() {
    if (children.isEmpty()) {
      return null;
    }
    return children.get(0);
  }

  /**
   * Gets the child node with the id, only returns the first occurance of the
   * found child.
   *
   * @param id The child's ID to search for
   * @return The found child, null if nothing was found
   */
  public Node get(int id) {
    return get(id, 0);
  }

  public Node get(int id, boolean throwException) {
    if (throwException) {
      return get(id, 0);
    } else {
      try {
        return get(id, 0);
      } catch (RuntimeException re) {
        return null;
      }
    }
  }

  /**
   * Gets the child node with the id. Since there could be duplicates or
   * multiple child nodes with the same ID, occur will tell the method to skip a
   * certain number of occurrences of the given ID. If occur is 1 then it will
   * skip the first occurrence of the search.
   *
   * @param id The id to search for
   * @param occur The number of times to skip a duplicate
   * @return The child node of this parent, null if it doesn't exist.
   */
  public Node get(int id, int occur) {
    for (int c = 0; c < children.size(); c++) {
      if (children.get(c).symbol().id() == id
              && occur == 0) {
        return children.get(c);
      } else if (children.get(c).symbol().id() == id
              && occur > 0) {
        occur--;
      }
    }
    throw new RuntimeException("The id '" + id
            + "' could not be found for the node '" + def + "' on line "
            + line);
  }

  public ArrayList<Node> getAll() {
    return children;
  }

  @Override
  public String toString() {
    return def.toString() + " "
            + ((children.isEmpty()) ? "" : (children.toString()));
  }
}
