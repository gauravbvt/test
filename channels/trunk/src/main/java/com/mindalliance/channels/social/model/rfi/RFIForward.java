package com.mindalliance.channels.social.model.rfi;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;

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
public class RFIForward extends AbstractPersistentPlanObject {

    @Column(length=1024)
    private String forwardToEmail;

    private Date dateForwarded;

    @Column(length=3000)
    private String message = "";

    @ManyToOne
    private RFI rfi;

    public RFIForward() {}

    public RFIForward( Plan plan, ChannelsUser user, RFI rfi, String forwardToEmail, String message  ) {
        super( plan.getUri(), plan.getVersion(), user.getUsername()  );
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
        return message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }
}
