
package rowdy;

import growdy.Node;
import growdy.NonTerminal;
import growdy.Symbol;
import growdy.Terminal;
/**
 *
 * @author Richard
 */
public abstract class BaseNode extends Node<BaseNode, Object>{

  protected static RowdyInstance instance;
  protected boolean isCompressable, reduce, criticalTerminal;
  private int objectMutable;
  public static final int UNSAFE_MUTABLE = 101, SAFE_MUTABLE = 100, UNKNOWN = 0;
  
  public BaseNode(Symbol symbol, int lineNumber) {
    super(symbol, lineNumber);
    isCompressable = symbol instanceof NonTerminal;
    reduce = false;
    criticalTerminal = false;
    objectMutable = UNKNOWN;
  }
  
  public void setObjectMutable(int objectMutable) {
    this.objectMutable = objectMutable;
  }
  
  public boolean isObjectSafe() {
    return objectMutable != UNSAFE_MUTABLE;
  }
  public void setAsNonCompressable() {
    isCompressable = false;
  }
  
  public void setAsCriticalTerminal() {
    criticalTerminal = true;
  }
  
  public boolean isCriticalTerminal() {
    return this.criticalTerminal;
  }
  
  public void reduce() {
    this.reduce = true;
  }
  
  public boolean canReduce() {
    return this.reduce;
  }
  
  public boolean isCompressable() {
    return this.isCompressable;
  }
  
  public final Object execute() {
    return execute(null);
  }
  
  @Override
  public BaseNode copy() {
    Symbol cSymbol = null;
    if (this.symbol instanceof Terminal) {
      cSymbol = ((Terminal)symbol).copy();
    } else if (this.symbol instanceof NonTerminal) {
      cSymbol = ((NonTerminal)symbol).copy();
    }
    
    BaseNode copy = Rowdy.nodeFactory.getNode(cSymbol, this.line);
    copy.setSeqActive(this.seqActive);
    copy.setTrimmable(this.trimmable);
    copy.setChildren(copyChildren());
    
    return copy;
  }
//  @Override
//  public String toString() {
//    return symbol.toString();
//  }
}
