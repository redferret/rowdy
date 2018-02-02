
package rowdy.exceptions;

/**
 *
 * @author Richard
 */
public class MainNotFoundException extends Throwable {

  public MainNotFoundException(String main_method_not_found) {
    super(main_method_not_found);
  }
  
}
