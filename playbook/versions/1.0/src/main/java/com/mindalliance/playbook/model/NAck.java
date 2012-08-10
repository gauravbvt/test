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

    private static final long serialVersionUID = 6158119150906794891L;

    //
    // Constructors
    //
    public NAck() {
    }

    public NAck( ConfirmationReq request, String reason ) {
        super( request, reason );
    }

    @Override
    public boolean isAck() {
        return false;
    }
}
