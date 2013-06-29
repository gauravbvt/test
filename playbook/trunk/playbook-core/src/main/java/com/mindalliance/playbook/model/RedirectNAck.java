/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Redirect refusal.
 */
@Entity
@DiscriminatorValue( "redirectNack" )
public class RedirectNAck extends NAck {

    //
    // Constructors
    //
    public RedirectNAck() {
    }
}
