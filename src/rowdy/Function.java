package rowdy;

import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;

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
  private boolean isDynamic;
  private RowdyObject parent;
  public Stack<BaseNode> activeLoops;

  public Function(String name, HashMap<String, Value> params, int lineCalledOn) {
    this.name = name;
    this.symbolTable = new SymbolTable(params, this);
    activeLoops = new Stack<>();
    funcReturnValue = null;
    this.lineCalledOn = lineCalledOn;
    parent = null;
    isDynamic = false;
  }
  
  /**
   * Sets this function with a dynamic scope
   */
  public void setAsDynamic() {
    isDynamic = true;
  }
  
  public boolean isDynamic(){
    return this.isDynamic;
  }
  
  /**
   * Determines if this function is part of a class
   * @return 
   */
  public boolean isMemberFunction() {
    return parent != null;
  }

  /**
   * If this function is part of a class the parent is referenced making
   * this function a member of that object.
   * @param parent 
   */
  public void setClassObject(RowdyObject parent) {
    this.parent = parent;
  }
  
  /**
   * Gets the reference of the object this function belongs to
   * @return 
   */
  public RowdyObject getClassObject() {
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
    if (funcReturnValue == null) {
      return new Value(null, false);
    }
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
