/*
 * Created on May 4, 2007
 */
package com.mindalliance.channels.data.elements.scenario;

import com.mindalliance.channels.data.components.Cause;
import com.mindalliance.channels.util.GUID;

/**
 * A request is a prompt to share information. A request can be passed
 * along through intermediates.
 * 
 * @author jf
 */
public class Request extends Communication {

    private Cause<Request> cause; // if not null then a request is
                                    // being passed along

    public Request() {
        super();
    }

    public Request( GUID guid ) {
        super( guid );
    }

    public Cause<Request> getCause() {
        return cause;
    }

    /**
     * @param cause the cause to set
     */
    public void setCause( Cause cause ) {
        this.cause = (Cause<Request>) cause;
    }

}
