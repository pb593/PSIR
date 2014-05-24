import session.*;
import requestlistener.*;

/**
 * Able to deactivate server.
 *
 * @author Ashley Newson <ashleynewson@smartsim.org.uk>
 * @since  2014-05-24
 */
public class AdminRequestHandler {
    private RequestListener listener;
    
    public AdminRequestHandler (Session session, RequestListener listener) {
	super (session);
	this.listener = listener;
    }

    /**
     * Implements the communication protocol for server control.
     */
    public void run () {
	while (true) {
	    try {
		session.send_str ("WhatDoYouWant");
		session.submit ();
		session.receive ();
		String command = session.get_str (0);
	    } catch (Exception e) {
		System.err.printf ("AdminRequestHandler.run(): %s\n", e.getMessage());
		session.close ();
		return;
	    }

	    switch (command) {
	    case "Start":
		listener.start_server ();
		session.send_str ("Info");
		session.send_str ("The server is now accepting requests.");
		session.submit ();
		break;
	    case "Stop":
		listener.stop_server ();
		session.send_str ("Info");
		session.send_str ("The server is no longer accepting requests.");
		session.submit ();
		break;
	    case "Close":
		session.send_str ("Info");
		session.send_str ("Goodbye.");
		session.submit ();
		session.close ();
		break;
	    }
	}
    }
}
