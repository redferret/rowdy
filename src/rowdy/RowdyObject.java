
package rowdy;

import java.util.HashMap;


/**
 *
 * @author Richard
 */
public class RowdyObject {
  private final Function functionBody;
  private final SymbolTable instanceVariables;
  
  public RowdyObject(Function functionBody) {
    this.functionBody = functionBody;
    this.instanceVariables = new SymbolTable(new HashMap<>());
  }

  public Function getFunctionBody() {
    return functionBody;
  }
  
  public SymbolTable getSymbolTable() {
    return this.instanceVariables;
  }
}
