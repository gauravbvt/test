// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.profiles.InferableObject;
import com.mindalliance.channels.data.support.GUID;

/**
 * A sequence of commnunications that partially or completely fulfill
 * a sharing need.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Flow extends InferableObject {

    private SharingNeed sharingNeed;
    private List<Communication> communications =
                    new ArrayList<Communication>();

    /**
     * Default constructor.
     */
    public Flow() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Flow( GUID guid ) {
        super( guid );
    }

    /**
     * Return the communications that realize the sharing need.
     */
    public List<Communication> getCommunications() {
        return communications;
    }

    /**
     * Set the communications that realize the sharing need.
     * @param communications the communications to set
     */
    public void setCommunications( List<Communication> communications ) {
        this.communications = communications;
    }

    /**
     * Add a communication.
     * @param communication the communication
     */
    public void addCommunication( Communication communication ) {
        this.communications.add( communication );
    }

    /**
     * Remove a communication.
     * @param communication the communication
     */
    public void removeCommunication( Communication communication ) {
        this.communications.remove( communication );
    }

    /**
     * Return the sharing need.
     */
    public SharingNeed getSharingNeed() {
        return sharingNeed;
    }

    /**
     * Set the sharing need.
     * @param sharingNeed the sharingNeed
     */
    public void setSharingNeed( SharingNeed sharingNeed ) {
        this.sharingNeed = sharingNeed;
    }

}
