package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/21/12
 * Time: 8:25 PM
 */
@Entity
public class RFIForward extends AbstractPersistentChannelsObject {

    @Column(length=1024)
    private String forwardToEmail;

    private Date dateForwarded;

    @Column(length=3000)
    private String message = "";

    private boolean notified;


    @ManyToOne
    private RFI rfi;

    public RFIForward() {}

    public RFIForward( PlanCommunity planCommunity, ChannelsUser user, RFI rfi, String forwardToEmail, String message  ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), user.getUsername()  );
        this.rfi = rfi;
        this.forwardToEmail = forwardToEmail;
        this.message = message;
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

    public RFI getRfi() {
        return rfi;
    }

    public void setRfi( RFI rfi ) {
        this.rfi = rfi;
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
