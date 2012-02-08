/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * A step involving receiving some information from another party.
 */
@Entity
@DiscriminatorValue( "receive" )
public class Receive extends Collaboration {

    private static final long serialVersionUID = -1183800547101079417L;
    
    private boolean startingPlay;

    //
    // Constructors
    //
    public Receive() {
    }

    public Receive( Step step ) {
        super( step );
    }

    public Receive( Play play ) {
        super( play );
    }

    @Override
    public Type getType() {
        return Type.RECEIVE;
    }

    public boolean isStartingPlay() {
        return startingPlay;
    }

    public void setStartingPlay( boolean startingPlay ) {
        this.startingPlay = startingPlay;
    }
}
