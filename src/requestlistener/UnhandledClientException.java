package requestlistener;

/**
 * If a client has no RequestHandler registered in the listener, it will
 * raise this exception.
 */
public class UnhandledClientException extends Exception {
    public UnhandledClientException (String message) {
	super (message);
    }
}
