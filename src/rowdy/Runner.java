package rowdy;
import java.util.List;
/**
 *
 * @author Richard
 */
public interface Runner<T> {
    public void execute(List<T> program);
}
