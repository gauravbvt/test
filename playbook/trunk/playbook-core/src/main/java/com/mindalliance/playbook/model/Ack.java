/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * An agreement for a request.
 */
@Entity
@DiscriminatorValue( "ack" )
public class Ack extends ConfirmationAck {

    private static final long serialVersionUID = 5441056549097062102L;

    @OneToOne( optional = true ) 
    private Collaboration step;
    
    //
    // Constructors
    //
    public Ack() {
    }

    public Ack( ConfirmationReq request, Collaboration step ) {
        super( request );
        this.step = step;
    }

    @Override
    public boolean isAck() {
        return true;
    }

    /**
     * @return the step that was created as a result of answering yes to a request.
     */
    public Collaboration getStep() {
        return step;
    }
}
