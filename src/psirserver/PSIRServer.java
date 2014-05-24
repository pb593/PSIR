/**
 * Main entry point and high-level operations
 *
 * @author Ashley Newson <ashleynewson@smartsim.org.uk>
 * @since  2014-05-24
 */
public class PSIRServer {
    public static void main (String[] args) {
	RequestListener listener = new RequestListener (7747);

	listener.add_handler ("Admin", new RequestHandlerReg(){
		public RequestHandler create (Session session) {
		    return new RequestHandler (session, listener);
		}
	    });

	listener.start_server ();
	
	// Wait loop
	while (listener.check_running()) {
	    Thread.sleep (100);
	}
    }
}
