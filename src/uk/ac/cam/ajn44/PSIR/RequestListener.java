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
    private List<RequestHandler> handlers;
    /**
     * Variable marking whether server thread should continue running.
     */
    private boolean serverRunning = false;
    
    /**
     * Creates the listener, but does not start listening.
     *
     * @param port Port to listen on.
     */
    public RequestListener (int port = 7747) {
        listeningPort = port;
        handlers = new LinkedList<RequestHandler>();
    }

    /**
     * Start listening for connections.
     */
    public void start_server () {
        serverRunning = true;
        // Spawns run() as a new thread.
        this.start();
    }

    /**
     * Stops listening for further connections, but does not end current
     * sessions.
     */
    public void stop_server () {
        // run() will see this has changed.
        serverRunning = false;
        // Causes the thread running in the run function to interupt.
        this.interupt();
    }

    /**
     * Implements Thread's run function (spawned when new thread
     * created). Main server listen-connect loop.
     */
    private void run () {
        try {
            ServerSocket serverSocket = new ServerSocket (listeningPort);
            while (serverRunning) {
                Socket socket = serverSocket.accept();
                // Start all handling of requests in a new thread so as
                // not to keep others waiting.
                (new Thread () {
                        // Create a Session for remainder of socket
                        // operations
                        session = new Session (socket);
                        session.send_arg ("IAmA");
                        session.send_arg ("PSIRServer");
                        session.send_arg (Server.version_string);
                        
                        // Identify server to client
                        
                    }).start();
            }
        }
    }

    /**
     * Adds a RequestHandler, which can take on certain request
     * responsibilities.
     *
     * @param handler The handler to add.
     */
    public void add_handler (RequestHandler handler) {
        handlers.add (handler);
    }
}
