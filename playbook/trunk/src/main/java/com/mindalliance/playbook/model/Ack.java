/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * An agreement for a request.
 */
@Entity
@DiscriminatorValue( "ack" )
public class Ack extends ConfirmationAck {

    //
    // Constructors
    //
    public Ack() {
    }
}
