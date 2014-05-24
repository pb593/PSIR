package requestlistener;

/**
 * There was an unexpected command in the communication.
 */
public class ProtocolMismatchException extends Exception {
    public ProtocolMismatchException (String message) {
	super (message);
    }
}
