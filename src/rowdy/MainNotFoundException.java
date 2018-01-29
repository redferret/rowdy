
package rowdy;

/**
 *
 * @author Richard
 */
public class MainNotFoundException extends RuntimeException {

  public MainNotFoundException(String main_method_not_found) {
    super(main_method_not_found);
  }
  
}
