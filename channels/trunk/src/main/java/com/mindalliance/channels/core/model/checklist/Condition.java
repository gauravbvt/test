package com.mindalliance.channels.core.model.checklist;

/**
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

    public abstract boolean isLocalCondition();

    public abstract String getLabel();

    @Override
    public boolean isCondition() {
        return true;
    }

    @Override
    public Condition getCondition() {
        return this;
    }

 }
