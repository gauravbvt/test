/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * A "no" answer to a request.
 */
@Entity
@DiscriminatorValue( "nack" )
public class NAck extends ConfirmationAck {

    private String reason;

    //
    // Constructors
    //
    public NAck() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason( String reason ) {
        this.reason = reason;
    }
}
