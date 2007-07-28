// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import com.mindalliance.channels.data.support.GUID;

/**
 * Request for certification by an applicable authority.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision$
 */
public class CertificationRequest extends UserRequest {

    /**
     * KInd of certification request.
     */
    enum Kind {
        /** An identity certification request. */
        IDENTIY,

        /** A role certification request. */
        ROLE
    };

    private GUID target;

    /**
     * Default constructor.
     */
    public CertificationRequest() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public CertificationRequest( GUID guid ) {
        super( guid );
    }
    /**
     * Return the GUID of either UserProfile or Person.
     */
    public GUID getTarget() {
        return target;
    }

    /**
     * Set the target guid.
     * @param target the target to set
     */
    public void setTarget( GUID target ) {
        this.target = target;
    }
}
