package com.mindalliance.channels.pages.profiles;

import com.mindalliance.channels.Part;
import com.mindalliance.channels.Flow;

import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 1:50:39 PM
 */
public class Play implements Serializable {

    private Part part;
    private Flow flow;
    private boolean isSend;

    public Play( Part part, Flow flow, boolean isSend) {
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

    public Part getOtherPart() {
       Part otherPart = null;
       if (isSend) {
           if (flow.getTarget().isPart()) {
               otherPart = (Part)flow.getTarget();
           }
       }
       else {
            if (flow.getSource().isPart()) {
                otherPart = (Part)flow.getSource();
            }
        }
        return otherPart;
    }

    public String getKind() {
        StringBuilder sb = new StringBuilder();
        if (isSend) {
            sb.append( flow.isAskedFor() ? "answer" : "notify" );
            if (flow.getMaxDelay() != null && !flow.getMaxDelay().isEmpty()) {
                sb.append(" within ");
                sb.append(flow.getMaxDelay());
            }

        }
        else {
            sb.append(flow.isAskedFor() ? "ask" : "receive");
        }
        return sb.toString();
    }

    public String getCriticality() {
        return flow.isCritical() ? "critical" : "";
    }

}
