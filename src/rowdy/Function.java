package rowdy;

import java.util.HashMap;
import java.util.Objects;

/**
 * A function in Rowdy always returns a value, has it's own symbol table, id
 * and the line it was called on. Functions are also used to initialize 
 * RowdyObjects.
 * @author Richard DeSilvey
 */
public class Function {

  private Value funcReturnValue;
  private final String name;
  private final int lineCalledOn;
  private final SymbolTable symbolTable;
  private boolean isAnonymous;
  private RowdyObject parent;

  public Function(String name, HashMap<String, Value> params, int lineCalledOn) {
    this.name = name;
    this.symbolTable = new SymbolTable(params);
    funcReturnValue = null;
    this.lineCalledOn = lineCalledOn;
    parent = null;
    isAnonymous = false;
  }
  
  public void setAsAnonymous() {
    isAnonymous = true;
  }
  
  public boolean isAnonymous(){
    return this.isAnonymous;
  }
  
  public boolean isIsMemberFunction() {
    return parent != null;
  }

  public void setParent(RowdyObject parent) {
    this.parent = parent;
  }
  
  public RowdyObject getParent() {
    return parent;
  }
  
  public int getLineCalledOn() {
    return lineCalledOn;
  }

  public void setReturnValue(Value value) {
    funcReturnValue = value;
  }

  public SymbolTable getSymbolTable() {
    return this.symbolTable;
  }

  public String getName() {
    return name;
  }

  public Value getReturnValue() {
    return funcReturnValue;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Function){
      Function other = (Function)obj;
      if (other.name.equals(this.name)){
        return true;
      } else if (other == this){
        return true;
      }
    } 
    
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 89 * hash + Objects.hashCode(this.name);
    return hash;
  }
  
  @Override
  public String toString() {
    return name;
  }
}
