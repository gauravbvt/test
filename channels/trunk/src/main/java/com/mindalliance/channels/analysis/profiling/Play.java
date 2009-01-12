package com.mindalliance.channels.analysis.profiling;

import com.mindalliance.channels.Part;
import com.mindalliance.channels.Flow;

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
     * Gets the other part in the flow's name or an empty string if none
     * @return a string
     */
    public String getOtherPartName() {
        Part otherPart = getOtherPart();
        return (otherPart == null) ? "" : otherPart.getName();
    }

    /**
     * Get a string description of the kind of communication
     * including max delay if applicable
     * @return a string description of the communication
     */
    public String getKind() {
        StringBuilder sb = new StringBuilder();
        if ( isSend ) {
            sb.append( flow.isAskedFor() ? "answer" : "notify" );
            if ( flow.getMaxDelay() != null && !flow.getMaxDelay().isEmpty() ) {
                sb.append( " within " );
                sb.append( flow.getMaxDelay() );
            }

        } else {
            sb.append( flow.isAskedFor() ? "ask" : "receive" );
        }
        return sb.toString();
    }

    /**
     * Get string indicator of criticality
     * @return a string
     */
    public String getCriticality() {
        return flow.isCritical() ? "critical" : "";
    }

    /**
     * Whether a flow should be included in a playbook
     * @param flow a flow
     * @return boolean
     */
    public static boolean hasPlay( Flow flow ) {
        return flow.getSource().isPart() && flow.getTarget().isPart();
    }
}
