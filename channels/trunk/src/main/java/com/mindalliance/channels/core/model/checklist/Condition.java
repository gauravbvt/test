package com.mindalliance.channels.core.model.checklist;

import java.io.Serializable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/13
 * Time: 11:50 PM
 */
public abstract class Condition implements Serializable {

    protected abstract String getRef();

    public abstract boolean isEventTimingCondition();

    public abstract boolean isGoalCondition();

    public abstract boolean isLocalCondition();

    public abstract String getLabel();
}
