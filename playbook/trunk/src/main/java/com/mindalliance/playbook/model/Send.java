/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * A step involving sending some information to another party.
 */
@Entity
@DiscriminatorValue( "send" )
public class Send extends Collaboration {

    private static final long serialVersionUID = -4088623033947047932L;

    //
    // Constructors
    //
    public Send() {
    }

    public Send( Step step ) {
        super( step );
    }

    public Send( Play play ) {
        super( play );
    }

    @Override
    public boolean isSend() {
        return true;
    }

    @Override
    public Type getType() {
        return Type.SEND;
    }

    @Override
    @Transient
    public String getMediumString() {
        Medium using = getUsing();
        return using == null ? "Somehow" : using.getDescription( getWith(), false );
    }
}
