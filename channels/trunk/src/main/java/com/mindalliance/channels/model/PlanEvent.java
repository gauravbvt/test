package com.mindalliance.channels.model;

/**
 * A planned-for event.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 30, 2009
 * Time: 3:49:53 PM
 */
public class PlanEvent extends ModelObject {
    /**
     * Where the event is considered to occur.
     */
    private Place scope;

    public Place getScope() {
        return scope;
    }

    public void setScope( Place scope ) {
        this.scope = scope;
    }

}
