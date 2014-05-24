package requestlistener;

import session.*;
import requesthandler.*;

/**
 * Used as a means of creating a request handler.
 */
public interface RequestHandlerReg {
    /**
     * Constructs a request handler as appropriate
     */
    public RequestHandler create (Session session);
}
