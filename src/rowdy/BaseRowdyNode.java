
package rowdy;

import growdy.Node;
import growdy.NonTerminal;
import growdy.Symbol;
import growdy.Terminal;
import rowdy.nodes.RowdyNode;

/**
 *
 * @author Richard
 */
public abstract class BaseRowdyNode extends Node<BaseRowdyNode, Value>{

  protected static RowdyInstance instance;
  protected boolean isCompressable;
  
  public BaseRowdyNode(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
    isCompressable = symbol instanceof NonTerminal;
  }

  public void setAsNonCompressable() {
    isCompressable = false;
  }
  
  public boolean isCompressable() {
    return this.isCompressable;
  }
  
  public final Value execute() {
    return execute(null);
  }
  
  @Override
  public BaseRowdyNode copy() {
    Symbol cSymbol = null;
    if (this.symbol instanceof Terminal) {
      cSymbol = ((Terminal)symbol).copy();
    } else if (this.symbol instanceof NonTerminal) {
      cSymbol = ((NonTerminal)symbol).copy();
    }
    
    BaseRowdyNode copy = Rowdy.nodeFactory.getNode(cSymbol, this.line);
    copy.setSeqActive(this.seqActive);
    copy.setTrimmable(this.trimmable);
    copy.setChildren(copyChildren());
    
    return copy;
  }
  
}
