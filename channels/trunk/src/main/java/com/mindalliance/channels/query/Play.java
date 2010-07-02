package com.mindalliance.channels.query;

import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.dao.User;

import java.io.Serializable;

/**
 * The sending or receiving of information by a part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 1:50:39 PM
 */
public class Play implements Serializable {

    /**
     * Used in calculating hashCode
     */
    private static int SEED = 31;


    /**
     * Part in play
     */
    private Part part;
    /**
     * Communication
     */
    private Flow flow;
    /**
     * Whether part is source
     */
    private boolean isSend;

    public Play( Part part, Flow flow, boolean isSend ) {
        this.part = part;
        this.flow = flow;
        this.isSend = isSend;
    }

    public Part getPart() {
        return part;
    }

    public void setPart( Part part ) {
        this.part = part;
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow( Flow flow ) {
        this.flow = flow;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend( boolean send ) {
        isSend = send;
    }

    /**
     * Get the other part in the flow
     *
     * @return a part
     */
    public Part getOtherPart() {
        Part otherPart = null;
        if ( isSend ) {
            if ( flow.getTarget().isPart() ) {
                otherPart = (Part) flow.getTarget();
            }
        } else {
            if ( flow.getSource().isPart() ) {
                otherPart = (Part) flow.getSource();
            }
        }
        return otherPart;
    }

    /**
     * Get a string description of the kind of communication
     * including max delay if applicable
     *
     * @return a string description of the communication
     */
    public String getKind() {
        if ( isSend ) {
            return flow.isAskedFor() ? "answer" : "notify";
        } else {
            return flow.isAskedFor() ? "ask" : "receive";
        }
    }

    /**
     * Get string indicator of "requiredness"
     *
     * @return a string
     */
    public String getRequiredness() {
        return flow.isRequired() ? "required" : "optional";
    }

    /**
     * Whether a flow should be included in a playbook
     *
     * @param flow a flow
     * @return boolean
     */
    public static boolean hasPlay( Flow flow ) {
        return flow.getSource().isPart() && flow.getTarget().isPart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof Play ) {
            Play play = (Play) obj;
            return flow == play.getFlow() && part == play.getPart();
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 1;
        if ( part != null ) hash = hash * SEED + part.hashCode();
        if ( flow != null ) hash = hash * SEED + flow.hashCode();
        return hash;
    }

    /**
     * Return the part in the play that equals or narrows a resourceSpec.
     * Assumes at least one part matches.
     * @param resourceSpec a resourceSpec
     * @return a part
     */
    public Part getPartFor( ResourceSpec resourceSpec ) {
        Part part = getPart();
        if ( part.resourceSpec().narrowsOrEquals( resourceSpec, User.plan() ) ) {
            return part;
        }
        else {
            return getOtherPart();
        }
    }
}
