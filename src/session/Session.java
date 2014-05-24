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
        Argument (byte[] data, boolean rawData) {
            this.data = data;
            this.rawData = rawData;
        }
        byte[] data;
        boolean rawData;
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
    public Session (Socket socket) {
        this.socket = socket;
        inputArgs = new ArrayList<Argument> ();
        outputArgs = new ArrayList<Argument> ();
        BufferedOutputStream output =
            new BufferedOutputStream(socket.getOutputStream());
        BufferedInputStream input =
            new BufferedInputStream(socket.getInputStream());
    }

    /**
     * Prepare to send a string argument
     */
    public void send_str (String str) {
        outputArgs.add (new Argument(str.getBytes("UTF-8"), false));
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
    public int get_arg_count (int arg) {
        return (inputArgs != null) ? inputArgs.size() : 0;
    }

    /**
     * Get a string argument from data received from client.
     */
    public byte[] get_str (int arg) throws IndexOutOfBoundsException {
        return String(inputArgs.get(arg).data, "UTF-8");
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
    public void receive () {
        byte b;
        boolean readingArgumentSet = true;
        List<Argument> argList = new ArrayList ();
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
                    if (quoting) {
                        arg.write ('#');
                    } else {
                        // Get raw byte count.
                        countByte = new ByteArrayOutputStream ();
                        while ((b = input.read()) != ' ') {
                            arg.write ();
                        }
                        int count = Integer.parseInt (countByte.toString());
                        countByte.close ();
                        byte[] rawData = new byte[count];
                        BufferedInputStream.read (rawData, 0, count);
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
                output.write ('#');
                byte[] count = String.valueOf(arg.data.length).getBytes("US-ASCII");
                output.write (count, 0, count.length - 1);
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
        }
        output.write ('\n');
        outputArgs = new List<Argument> ();
    }

    public void close () {
	socket.close ();
    }
}
