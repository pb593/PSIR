package psirserver;

import requestlistener.*;
import requesthandler.*;
import session.*;

import java.util.*;
import java.text.*;

/**
 * Main entry point and high-level operations
 *
 * @author Ashley Newson <ashleynewson@smartsim.org.uk>
 * @since  2014-05-24
 */
public class PSIRServer {
    public static final String version_string = "0.0.0";
    
    public static void main (String[] args) {
	final RequestListener listener = new RequestListener (7747);

	listener.add_handler ("Admin", new RequestHandlerReg(){
		public RequestHandler create (Session session) {
		    return new AdminRequestHandler (session, listener);
		}
	    });

	listener.start_server ();
	
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	// Wait loop
	while (listener.check_running()) {
	    Date date = new Date();
	    System.err.println(dateFormat.format(date));
	    try {
		Thread.sleep (1000);
	    } catch (InterruptedException e) {
		// This should not be run.
		System.err.println ("Server stopping due to interupt.");
		listener.stop_server ();
	    }
	}
    }
}
