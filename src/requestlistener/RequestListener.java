import session.*;
import requesthandler.*;

/**
 * Listens for incoming network connections and upon accepting network
 * connections calls the relevant request handler.
 *
 * @author Ashley Newson <ashleynewson@smartsim.org.uk>
 * @since  2014-05-23
 */
public class RequestListener extends Thread {
    /**
     * Port server listens on.
     */
    private final int listeningPort;
    /**
     * List of RequestHandlers which can be called based upon request.
     */
    private Map<String, RequestHandlerReg> handlers;
    /**
     * Variable marking whether server thread should continue running.
     */
    private boolean serverRunning = false;
    
    /**
     * Creates the listener, but does not start listening.
     *
     * @param port Port to listen on.
     */
    public RequestListener (int port) {
        listeningPort = port;
        handlers = new HashMap<String, RequestHandlerReg>();
    }

    /**
     * Start listening for connections.
     */
    public void start_server () {
        if (!serverRunning) {
            serverRunning = true;
            // Spawns run() as a new thread.
            this.start();
        }
    }

    /**
     * Check if the server is running.
     *
     * @return True if the server is running, false if not.
     */
    public void check_running () {
        return serverRunning;
    }

    /**
     * Stops listening for further connections, but does not end current
     * sessions.
     */
    public void stop_server () {
        if (serverRunning) {
            // run() will see this has changed.
            serverRunning = false;
            // Causes the thread running in the run function to
            // interupt.
            this.interupt();
        }
    }

    /**
     * Implements Thread's run function (spawned when new thread
     * created). Main server listen-connect loop.
     */
    private void run () {
        try {
            ServerSocket serverSocket = new ServerSocket (listeningPort);
            while (serverRunning) {
                try {
                    Socket socket = serverSocket.accept();
                    // Start all handling of requests in a new thread so
                    // as not to keep others waiting.
                    new Thread(){
                        // Create a Session for remainder of socket
                        // operations.
                        session = new Session (socket);
                        try {
                            // Identify server to client.
                            send_identity (session);
                            // Identify client type and run handler.
                            run_handler (session);
                        } catch (Exception e) {
                            System.err.printf ("RequestListener.run(): %s\n", e.getMessage());
                        } finally {
                            session.close ();
                        }
                    }.start();
                } catch (InterruptedException e) {
                    System.err.printf ("No longer accepting new connections.");
                }
            }
        } catch (Exception e) {
            System.err.printf ("RequestListener.run(): %s\n", e.getMessage());
        }
    }

    /**
     * Sends a server identification message.
     */
    private void send_identity (Session session) {
        session.send_str ("IAmA");
        session.send_str ("PSIRServer");
        session.send_str (PSIRServer.version_string);
        session.submit ();
    }

    /**
     * Identifies and runs relevant client request handler.
     */
    private void run_handler (Session session) throws ProtocolMismatchException, UnhandledClientException {
        // Request client type.
        session.send_str ("WhatAreYou");
        session.submit ();

        // Read client type.
        session.receive ();
        int argc = session.get_arg_count ();
        if (argc < 2) {
            throw ProtocolMismatchException ("Too few arguments identifying client");
        }
        
        String commandType = session.get_str (0);
        String clientType = session.get_str (1);
        if (!commandType.equals("IAmA")) {
            throw ProtocolMismatchException ("Unexpected command: " + commandType);
        }

        RequestHandlerReg handler = handlers.get (clientType);
        if (handler == null) {
            throw UnhandledClientException ("Unhandled client: " + clientType);
        }
        // Run handler.
        RequestHandler handler = handler.create (session);
        handler.run();
    }

    /**
     * Adds a RequestHandlerReg, which can take on certain request
     * responsibilities.
     *
     * @param name The type name sent by the client to determine service
     * type.
     * @param handler The handler to add.
     */
    public void add_handler (String name, RequestHandlerReg handler) {
        handlers.put (name, handler);
    }
}
