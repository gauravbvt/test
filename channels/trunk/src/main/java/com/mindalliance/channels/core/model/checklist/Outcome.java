package com.mindalliance.channels.core.model.checklist;

/**
 * An expected outcome of executing a step in a task checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/23/13
 * Time: 10:48 AM
 */
public abstract class Outcome extends AbstractChecklistElement {


    public abstract boolean isEventTimingOutcome();

    public abstract boolean isCapabilityCreatedOutcome();

    public abstract boolean isGoalAchievedOutcome();

    @Override
    public Outcome getOutcome() {
        return this;
    }

    @Override
    public boolean isOutcome() {
        return true;
    }
}
