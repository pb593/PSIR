import session.*;
import requestlistener.*;

/**
 * Handles communication with client and performs operations.
 *
 * @author Ashley Newson <ashleynewson@smartsim.org.uk>
 * @since  2014-05-24
 */
public abstract class RequestHandler {
    protected Session session;

    public RequestHandler (Session session) {
	this.session = session;
    }

    public abstract void run ();
}
