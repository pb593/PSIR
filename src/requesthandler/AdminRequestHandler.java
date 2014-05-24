package requesthandler;

import java.io.*;

import session.*;
import requestlistener.*;

/**
 * Able to deactivate server.
 *
 * @author Ashley Newson <ashleynewson@smartsim.org.uk>
 * @since  2014-05-24
 */
public class AdminRequestHandler extends RequestHandler {
    private RequestListener listener;
    
    public AdminRequestHandler (Session session, RequestListener listener) {
	super (session);
	this.listener = listener;
    }

    /**
     * Implements the communication protocol for server control.
     */
    public void run () {
	System.err.println ("An administrator has logged in.");
	while (true) {
	    String command;
	    
	    try {
		session.send_str ("WhatDoYouWant");
		session.submit ();
		session.receive ();
		command = session.get_str (0);
	    } catch (Exception e) {
		System.err.printf ("AdminRequestHandler.run(): %s\n", e.getMessage());
		session.close ();
		return;
	    }

	    switch (command) {
	    case "Start":
		listener.start_server ();
		try {
		    session.send_str ("Info");
		    session.send_str ("The server is now accepting requests.");
		    session.submit ();
		} catch (IOException e) {
		    System.err.printf ("AdminRequestHandler.run(): %s\n", e.getMessage());
		    session.close ();
		    return;
		}
		break;
	    case "Stop":
		try {
		    session.send_str ("Info");
		    session.send_str ("The server is no longer accepting requests.");
		    session.submit ();
		    session.send_str ("Info");
		    session.send_str ("Goodbye.");
		    session.submit ();
		} catch (IOException e) {
		    System.err.printf ("AdminRequestHandler.run(): %s\n", e.getMessage());
		    session.close ();
		}
		listener.stop_server ();
		return;
	    case "Close":
		try {
		    session.send_str ("Info");
		    session.send_str ("Goodbye.");
		    session.submit ();
		} catch (IOException e) {
		    System.err.printf ("AdminRequestHandler.run(): %s\n", e.getMessage());
		} finally {
		    session.close ();
		    return;
		}
	    default:
		try {
		    session.send_str ("Error");
		    session.send_str ("The command \"" + command + "\" was not recognised.");
		    session.submit ();
		} catch (IOException e) {
		    System.err.printf ("AdminRequestHandler.run(): %s\n", e.getMessage());
		    session.close ();
		    return;
		}
	    }
	}
    }
}
