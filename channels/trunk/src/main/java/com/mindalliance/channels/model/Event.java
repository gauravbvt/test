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
}
