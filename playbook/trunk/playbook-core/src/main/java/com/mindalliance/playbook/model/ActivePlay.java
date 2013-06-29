/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * An active play.
 */
@Entity
@DiscriminatorValue( "active" )
public class ActivePlay extends Play {

    private Date started;

    @ManyToOne
    private Play play;

    //
    // Constructors
    //
    public ActivePlay() {
    }

    public ActivePlay( Date started, Play play ) {
        this.started = started;
        this.play = play;
    }

    public Date getStarted() {
        return started;
    }

    public Play getPlay() {
        return play;
    }
}
