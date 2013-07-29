package com.mindalliance.channels.core.model.checklist;

/**
 * A condition that ought to be met before executing a step in a task checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/13
 * Time: 11:50 PM
 */
public abstract class Condition extends AbstractChecklistElement {

    public static final String IF = "If";
    public static final String UNLESS = "Unless";

    public abstract boolean isEventTimingCondition();

    public abstract boolean isGoalCondition();

    public abstract boolean isNeedSatisfiedCondition();

    public abstract boolean isLocalCondition();

    public abstract boolean isTaskFailedCondition();

    @Override
    public boolean isCondition() {
        return true;
    }

    @Override
    public Condition getCondition() {
        return this;
    }

    public abstract boolean matches( Outcome outcome );
}
