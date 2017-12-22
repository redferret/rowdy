package rowdy;
/**
 *
 * @author Richard DeSilvey
 */
public class Terminal extends Symbol {
    private String realValue;
    private Object boundTo;
    public Terminal(String symbol, int id) {
        super(symbol, id);
        set("");
    }
    public Terminal(String symbol, int id, String value) {
        super(symbol, id);
        set(value);
    }
    private void set(String value){
        realValue = value;
        boundTo = null;
    }
    public void bindTo(Object bindee){
        boundTo = bindee;
    }
    public void setValue(String value){
        realValue = value;
    }
    public String getName(){
        return realValue;
    }
    public Object getBinding() {
        return boundTo;
    }
    public String toString(){
        return getName() + "  " + realValue;
    }
}
