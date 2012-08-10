/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * A successful redirect confirmation.
 */
@Entity
@DiscriminatorValue( "redirect" )
public class RedirectAck extends ConfirmationAck {
    
    //
    // Constructors
    //
    public RedirectAck() {
    }

    @Override
    public boolean isAck() {
        return true;
    }
}
