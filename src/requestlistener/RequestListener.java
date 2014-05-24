package requestlistener;

import java.net.*;
import java.util.*;
import java.lang.*;
import java.io.*;

import session.*;
import requesthandler.*;
import requestlistener.*;
import psirserver.*;

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
        setDaemon (true);
    }

    /**
     * Start listening for connections.
     */
    public void start_server () {
        System.err.printf ("Starting server on port %s\n", listeningPort);
        if (!serverRunning) {
            serverRunning = true;
            // Spawns run() as a new thread.
            this.start();
        }
    }

    /**
     * Check if the server is running.
     *
     * @return true if the server is running, false if not.
     */
    public boolean check_running () {
        return serverRunning;
    }

    /**
     * Stops listening for further connections, and ends sessions.
     */
    public void stop_server () {
        System.err.printf ("Stopping server on port %s\n", listeningPort);
        if (serverRunning) {
            // run() will see this has changed.
            serverRunning = false;
            // Causes the thread running in the run function to
            // interupt.
            this.interrupt();
        }
    }

    /**
     * Implements Thread's run function (spawned when new thread
     * created). Main server listen-connect loop.
     */
    public void run () {
        try {
            ServerSocket serverSocket = new ServerSocket (listeningPort);
            while (serverRunning) {
                Socket socket;
                try {
                    // Only final for the given iteration.
                    socket = serverSocket.accept();
                } catch (Exception e) {
                    System.err.printf ("RequestListener.run(): Cannot accept connection : %s\n", e.getMessage());
                    stop_server ();
                    break;
                }
                // Start all handling of requests in a new thread so
                // as not to keep others waiting.
                new Thread(){
                    private Socket socket;

                    // Ensure that we definitely give socket to the
                    // thread before starting it.
                    void init_and_start (Socket socket) {
                        this.socket = socket;
                        this.start ();
                    }
                        
                    // Create a Session for remainder of socket
                    // operations.
                    public void run () {
                        try {
                            Session session = new Session (socket);
                            try {
                                // Identify server to client.
                                send_identity (session);
                                // Identify client type and run handler.
                                run_handler (session);
                            } catch (IOException e) {
                                System.err.printf ("RequestListener.run(): Unable to send identity: %s\n", e.getMessage());
                            } catch (ProtocolMismatchException e) {
                                System.err.printf ("RequestListener.run(): Protocol error: %s\n", e.getMessage());
                            } catch (UnhandledClientException e) {
                                System.err.printf ("RequestListener.run(): Client type unhandled : %s\n", e.getMessage());
                            }
                            session.close ();
                        } catch (IOException e) {
                            System.err.printf ("RequestListener.run(): Unable to start session: %s\n", e.getMessage());
                        }
                    }
                }.init_and_start(socket);
            }
        } catch (Exception e) {
            System.err.printf ("RequestListener.run(): %s\n", e.getMessage());
        }
    }

    /**
     * Sends a server identification message.
     */
    private void send_identity (Session session) throws IOException {
        session.send_str ("IAmA");
        session.send_str ("PSIRServer");
        session.send_str (PSIRServer.version_string);
        session.submit ();
    }

    /**
     * Identifies and runs relevant client request handler.
     */
    private void run_handler (Session session) throws ProtocolMismatchException, UnhandledClientException {
        try {
            // Request client type.
            session.send_str ("WhatAreYou");
            session.submit ();
            // Read client type.
            session.receive ();
        } catch (IOException e) {
            System.err.printf ("RequestListener.run_handler(): %s\n", e.getMessage());
            session.close ();
            return;
        }

        int argc = session.get_arg_count ();
        if (argc < 2) {
            throw new ProtocolMismatchException ("Too few arguments identifying client");
        }
        
        String commandType = session.get_str (0);
        String clientType = session.get_str (1);
        if (!commandType.equals("IAmA")) {
            throw new ProtocolMismatchException ("Unexpected command: " + commandType);
        }

        RequestHandlerReg handlerReg = handlers.get (clientType);
        if (handlerReg == null) {
            throw new UnhandledClientException ("Unhandled client: " + clientType);
        }
        // Run handler.
        RequestHandler handler = handlerReg.create (session);
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
