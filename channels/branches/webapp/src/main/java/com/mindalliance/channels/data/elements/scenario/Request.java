// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.scenario;

import com.mindalliance.channels.util.GUID;

/**
 * A request is a prompt to share information. A request can be passed
 * along through intermediates.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Request extends Communication<Request> {

    /**
     * Default constructor.
     */
    public Request() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Request( GUID guid ) {
        super( guid );
    }
}
