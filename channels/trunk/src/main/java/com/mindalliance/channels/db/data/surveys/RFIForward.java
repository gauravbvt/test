package com.mindalliance.channels.db.data.surveys;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.db.data.AbstractChannelsDocument;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * The forwarding of an RFI.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/30/13
 * Time: 11:55 AM
 */
@Document( collection = "surveys" )
public class RFIForward extends AbstractChannelsDocument {

    private String forwardToEmail;

    private Date dateForwarded;

    private String message = "";

    private boolean notified;

    private String rfiUid;


    public RFIForward() {}

    public RFIForward( PlanCommunity planCommunity,
                       ChannelsUser user,
                       RFI rfi,
                       String forwardToEmail,
                       String message ) {
        super( planCommunity.getUri(), planCommunity.getModelUri(), planCommunity.getModelVersion(), user.getUsername()  );
        setForwardToEmail( forwardToEmail );
        setMessage( message );
        rfiUid = rfi.getUid();
    }

    public String getRfiUid() {
        return rfiUid;
    }

    public void setRfiUid( String rfiUid ) {
        this.rfiUid = rfiUid;
    }

    // Assumes valid email
    public String getForwardToEmail() {
        return forwardToEmail;
    }

    public void setForwardToEmail( String forwardToEmail ) {
        this.forwardToEmail = forwardToEmail;
    }

    public Date getDateForwarded() {
        return dateForwarded;
    }

    public void setDateForwarded( Date dateForwarded ) {
        this.dateForwarded = dateForwarded;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified( boolean notified ) {
        this.notified = notified;
    }

}
