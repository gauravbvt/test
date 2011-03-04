package com.mindalliance.channels.social;

import com.mindalliance.channels.odb.PersistentObject;

/**
 * A planning event.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2010
 * Time: 2:04:08 PM
 */
public abstract class PlanningEvent extends PersistentObject {

    private long planId;

    PlanningEvent( long planId ) {
        super();
        this.planId = planId;
    }

    public long getPlanId() {
        return planId;
    }

    public boolean isPresenceEvent() {
        return false;
    }

    public boolean isCommandEvent() {
        return false;
    }

    public abstract String getUsername();
}
