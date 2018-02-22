
package rowdy.nodes;

import growdy.Node;
import growdy.NonTerminal;
import growdy.Symbol;
import growdy.Terminal;
import rowdy.Rowdy;
import rowdy.RowdyInstance;
import rowdy.Value;
import rowdy.exceptions.ConstantReassignmentException;


/**
 *
 * @author Richard
 */
public class RowdyNode extends Node<RowdyNode> {
  
  protected static RowdyInstance instance;
  
  public RowdyNode(Symbol def, int lineNumber) {
    super(def, lineNumber);
  }
  
  public final Value execute() throws ConstantReassignmentException {
    return execute(null);
  }
  
  public static void initRunner(RowdyInstance runner) {
    RowdyNode.instance = runner;
  }
  
  @Override
  public RowdyNode copy() {
    Symbol cSymbol = null;
    if (this.symbol instanceof Terminal) {
      cSymbol = ((Terminal)symbol).copy();
    } else if (this.symbol instanceof NonTerminal) {
      cSymbol = ((NonTerminal)symbol).copy();
    }
    
    RowdyNode copy = (RowdyNode) Rowdy.nodeFactory.getNode(cSymbol, this.line);
    copy.seqActive = this.seqActive;
    copy.trimmable = this.trimmable;
    copy.children = copyChildren();
    
    return copy;
  }
  
  /**
   * Override this method to implement your logic
   * @param cur
   * @param leftValue
   * @return
   * @throws ConstantReassignmentException 
   */
  public Value execute(Value leftValue) throws ConstantReassignmentException {
    return instance.fetch(leftValue, this);
  }
}
