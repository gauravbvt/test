// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import com.mindalliance.channels.support.GUID;

/**
 * Request to the proper authority to resolve a conflict about some
 * element.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class ResolutionRequest extends UserRequest {

    private GUID elementGUID;
    private boolean escalate;

    /**
     * Default constructor.
     */
    public ResolutionRequest() {
        super();
    }

    /**
     * Return the element GUID.
     */
    public GUID getElementGUID() {
        return elementGUID;
    }

    /**
     * Set the element GUID.
     * @param elementGUID the elementGUID to set
     */
    public void setElementGUID( GUID elementGUID ) {
        this.elementGUID = elementGUID;
    }

    /**
     * Return an indication that prior resolution requests about the
     * same element were not addressed to satisfaction.
     */
    public boolean isEscalate() {
        return escalate;
    }

    /**
     * Set the escalation state.
     * @param escalate the state
     */
    public void setEscalate( boolean escalate ) {
        this.escalate = escalate;
    }
}
