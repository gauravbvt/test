/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * A step consisting of activating another play.
 */
@Entity
@DiscriminatorValue( "subplay" )
public class Subplay extends Step {

    private static final long serialVersionUID = 5781531907814773795L;

    @ManyToOne
    private Play subplay;

    //
    // Constructors
    //
    public Subplay() {
    }

    public Subplay( Step step ) {
        super( step );
        
        if ( step.getType() == Type.SUBPLAY )
            subplay = ( (Subplay) step ).getSubplay();

    }

    @Override
    public Type getType() {
        return Type.SUBPLAY;
    }

    public Play getSubplay() {
        return subplay;
    }

    public void setSubplay( Play subplay ) {
        this.subplay = subplay;
    }
}
