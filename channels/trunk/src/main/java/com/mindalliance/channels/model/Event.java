// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

/**
 * Something of consequence that is true for some period of time.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 * @navassoc - - 1 ModelElement
 */
public class Event extends Occurence {

    private ModelElement cause;

    /**
     * Default constructor.
     */
    Event() {
        super();
    }

    /**
     * Convenience constructor.
     * @param scenario the scenario
     */
    public Event( Scenario scenario ) {
        super( scenario );
    }

    /**
     * Return the value of cause.
     */
    public ModelElement getCause() {
        return this.cause;
    }

    /**
     * Set the value of cause.
     * @param cause The new value of cause
     */
    public void setCause( ModelElement cause ) {
        this.cause = cause;
    }
}
