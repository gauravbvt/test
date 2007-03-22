// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

/**
 * Assertion that an agent would respond to an event.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @navassoc - how 1 Task
 */
public class RespondsTo extends Assertion {

    private Task how;

    /**
     * Default constructor.
     */
    public RespondsTo() {
        super();
    }

    /**
     * Convenience constructor.
     * @param scenario the scenario
     */
    public RespondsTo( Scenario scenario ) {
        super( scenario );
    }

    /**
     * Return the value of how.
     */
    public Task getHow() {
        return this.how;
    }

    /**
     * Set the value of how.
     * @param how The new value of how
     */
    public void setHow( Task how ) {
        this.how = how;
    }
}
