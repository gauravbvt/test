/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * A generic step in a play.
 */
@Entity
@DiscriminatorValue( "task" )
public class Task extends Step {

    private static final long serialVersionUID = -4872947557720416499L;

    //
    // Constructors
    //
    public Task() {
    }

    public Task( Play play ) {
        super( play );
    }

    /**
     * Initialize from another step.
     * @param step the step
     */
    public Task( Step step ) {
        super( step );
    }

    @Override
    public Type getType() {
        return Type.TASK;
    }
}
