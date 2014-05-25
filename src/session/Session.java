package session;

import java.net.*;
import java.util.*;
import java.io.*;

/**
 * Simplifies network communication using space separated variables.
 *
 * @author Ashley Newson <ashleynewson@smartsim.org.uk>
 * @since  2014-05-24
 */
public class Session {
    /**
     * Used to store data with a flag for if it is raw data.
     */
    private class Argument {
        Argument (byte[] data, boolean isRaw) {
            this.data = data;
            this.isRaw = isRaw;
        }
        byte[] data;
        boolean isRaw;
    }
    
    /**
     * Socket which communications are going through.
     */
    private Socket socket;
    /**
     * Allows easy writing out to the client through the socket.
     */
    private BufferedOutputStream output;
    /**
     * Allows easy reading from the client through the socket.
     */
    private BufferedInputStream input;

    /**
     * The current list of arguments to send once submitted.
     */
    private List<Argument> outputArgs;
    /**
     * The last arguments to be read since a call to receive().
     */
    private List<Argument> inputArgs;
    
    /**
     * Prepares for sending data over the given socket.
     *
     * @param socket Socket which communications are going through.
     */
    public Session (Socket socket) throws IOException {
        this.socket = socket;
        inputArgs = new ArrayList<Argument> ();
        outputArgs = new ArrayList<Argument> ();
        output = new BufferedOutputStream(socket.getOutputStream());
        input = new BufferedInputStream(socket.getInputStream());
	System.err.printf ("Session started.\n");
    }

    /**
     * Prepare to send a string argument
     */
    public void send_str (String str) {
        try {
            outputArgs.add (new Argument(str.getBytes("UTF-8"), false));
        } catch (UnsupportedEncodingException e) {
            System.err.println ("FATAL: UTF-8 is not supported on this system!\n");
            close ();
            System.exit (1);
        }
    }

    /**
     * Prepare to send a data argument
     */
    public void send_data (byte[] data) {
        outputArgs.add (new Argument(Arrays.copyOf(data, data.length), true));
    }

    /**
     * Get a string argument from data received from client.
     */
    public int get_arg_count () {
        return (inputArgs != null) ? inputArgs.size() : 0;
    }

    /**
     * Get a string argument from data received from client.
     */
    public String get_str (int arg) throws IndexOutOfBoundsException {
        try {
            return new String(inputArgs.get(arg).data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println ("FATAL: UTF-8 is not supported on this system!\n");
            close ();
            System.exit (1);
        }
        return null; // Compiler complained.
    }

    /**
     * Get a data argument from data received from client.
     */
    public byte[] get_data (int arg) throws IndexOutOfBoundsException {
        return Arrays.copyOf(inputArgs.get(arg).data, inputArgs.get(arg).data.length);
    }

    /**
     * Waits for input from the client and organises data into
     * arguments stored in inputArgs.
     */
    public void receive () throws IOException {
        int b;
        boolean readingArgumentSet = true;
        List<Argument> argList = new ArrayList<Argument> ();
        while (readingArgumentSet) {
            boolean readingArgument = true;
            ByteArrayOutputStream arg = new ByteArrayOutputStream ();
            boolean quoting = false;
            // Whether the last character was a double quote.
            boolean quoted = false;
            // Whether we should ignore whitespace.
            boolean ignoreSpace = true;
            byte[] argData;
            // Whether raw data was ever read.
            boolean isRaw = false;
            
            while (readingArgument) {
                b = input.read ();
                switch (b) {
                case '\n':
                    quoted = false;
                    if (quoting) {
                        arg.write (b);
                    } else {
                        readingArgument = false;
                        readingArgumentSet = false;
                    }
                    break;
                case '\r':
                case ' ':
                case '\t':
                    quoted = false;
                    if (quoting) {
                        arg.write (b);
                    } else {
                        if (!ignoreSpace) {
                            readingArgument = false;
                        }
                    }
                    break;
                case '#':
		    quoted = false;
                    if (quoting) {
                        arg.write ('#');
                    } else {
                        // Get raw byte count.
                        ByteArrayOutputStream countByte = new ByteArrayOutputStream ();
                        while ((b = input.read()) != ' ') {
                            countByte.write (b);
                        }
                        int count = Integer.parseInt (countByte.toString());
                        countByte.close ();
                        // Get raw data.
                        byte[] rawData = new byte[count];
                        input.read (rawData, 0, count);
                        arg.write (rawData, 0, count);
                        isRaw = true;
                        readingArgument = false;
                    }
                    break;
                case '\"':
                    ignoreSpace = false;
                    if (quoted) {
                        arg.write ('\"');
                    }
                    quoted = !quoted;
                    quoting = !quoting;
                    break;
                default:
		    quoted = false;
                    ignoreSpace = false;
                    arg.write (b);
                    break;
                }
            }
            argData = arg.toByteArray();
            argList.add (new Argument(argData, isRaw));
        }
        inputArgs = argList;
    }

    /**
     * Sends the arguments stored in outputArgs to the client
     */
    public void submit () throws IOException {
        if (outputArgs == null) {
            output.write ('\n');
        }
        // Whether the current argument is the first.
        boolean command = true;
        for (Argument arg : outputArgs) {
            if (!command) {
                output.write (' ');
            }
            if (arg.isRaw) {
                byte[] count = null;
                output.write ('#');
                try {
                    count = String.valueOf(arg.data.length).getBytes("US-ASCII");
                } catch (UnsupportedEncodingException e) {
                    System.err.println ("FATAL: US-ASCII is not supported on this system!\n");
                    close ();
                    System.exit (1);
                }
                output.write (count, 0, count.length);
                output.write (' ');
                output.write (arg.data, 0, arg.data.length);
            } else {
                boolean needQuotes = false;
                for (int b : arg.data) {
                    switch (b) {
                    case ' ':
                    case '\t':
                    case '\n':
                    case '\r':
                    case '#':
                        needQuotes = true;
                        break;
                    }
                }
                if (needQuotes) {
                    output.write ('\"');
                }
                for (int b : arg.data) {
                    switch (b) {
                    case '\"':
                        output.write ('\"');
                        output.write ('\"');
                        break;
                    default:
                        output.write (b);
                        break;
                    }
                }
                if (needQuotes) {
                    output.write ('\"');
                }
            }
	    command = false;
        }
        output.write ('\n');
        output.flush ();
        outputArgs = new ArrayList<Argument> ();
    }

    public void close () {
        try {
	    input.close ();
	    output.close ();
            socket.close ();
        } catch (IOException e) {
            System.err.printf ("Error closing socket: %s\n", e);
        }
	System.err.printf ("Session Ended.\n");
    }
}
