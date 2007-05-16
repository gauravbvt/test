/*
 * Created on May 3, 2007
 */
package com.mindalliance.channels.data.user;

import com.mindalliance.channels.util.GUID;

/**
 * Request to the proper authority to resolve a conflict about some
 * element.
 * 
 * @author jf
 */
public class ResolutionRequest extends UserRequest {

    private GUID elementGUID;
    private boolean escalate; // indicates that prior resolution
                                // requests about the same element
                                // were not addressed to satisfaction.

    public ResolutionRequest() {
        super();
    }

    /**
     * @return the elementGUID
     */
    public GUID getElementGUID() {
        return elementGUID;
    }

    /**
     * @param elementGUID the elementGUID to set
     */
    public void setElementGUID( GUID elementGUID ) {
        this.elementGUID = elementGUID;
    }

    /**
     * @return the escalate
     */
    public boolean isEscalate() {
        return escalate;
    }

    /**
     * @param escalate the escalate to set
     */
    public void setEscalate( boolean escalate ) {
        this.escalate = escalate;
    }
}
