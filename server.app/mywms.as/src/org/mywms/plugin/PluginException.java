/**
 * 
 */
package org.mywms.plugin;

/**
 * Is thrown, if a plugin could not be resolved.
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Olaf Krause</a>
 * @version $Revision: 442 $ provided by $Author: okrause $
 */
public class PluginException
    extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new PluginException.
     */
    public PluginException() {
        super();
    }

    /**
     * Creates a new PluginException.
     * 
     * @param message a descritive message
     */
    public PluginException(String message) {
        super(message);
    }

    /**
     * Creates a new PluginException.
     * 
     * @param message a descriptive message
     * @param cause the (root) cause of the exception
     */
    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new PluginException.
     * 
     * @param cause the (root) cause of the exceptions
     */
    public PluginException(Throwable cause) {
        super(cause);
    }

}
