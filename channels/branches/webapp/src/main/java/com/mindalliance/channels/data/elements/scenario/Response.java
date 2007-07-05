// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.scenario;

import com.mindalliance.channels.util.GUID;

/**
 * A response is a sharing of information prompted by a request. Note
 * that upon being responded, the recipient implicitly asserts a Known
 * on the Knowledge caused by the response.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Response extends Communication<Request> {

    /**
     * Default constructor.
     */
    public Response() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Response( GUID guid ) {
        super( guid );
    }
}
