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
 * An step of an active play.
 */
@Entity
@DiscriminatorValue( "todo" )
public class Todo extends Step {

    private static final long serialVersionUID = -3820295179903249960L;

    private boolean done;

    private boolean failed;

    private Date started;

    private Date ended;

    @ManyToOne
    private Step step;

    @ManyToOne
    private ActivePlay activePlay;

    //
    // Constructors
    //
    public Todo() {
    }

    @Override
    public Type getType() {
        return step.getType();
    }

    public boolean isDone() {
        return done;
    }

    public void setDone( boolean done ) {
        this.done = done;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed( boolean failed ) {
        this.failed = failed;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted( Date started ) {
        this.started = started;
    }

    public Date getEnded() {
        return ended;
    }

    public void setEnded( Date ended ) {
        this.ended = ended;
    }

    public Step getStep() {
        return step;
    }

    public void setStep( Step step ) {
        this.step = step;
    }

    public ActivePlay getActivePlay() {
        return activePlay;
    }

    public void setActivePlay( ActivePlay activePlay ) {
        this.activePlay = activePlay;
    }
}
