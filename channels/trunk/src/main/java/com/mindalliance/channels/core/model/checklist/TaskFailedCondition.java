package com.mindalliance.channels.core.model.checklist;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/29/13
 * Time: 10:18 AM
 */
public class TaskFailedCondition extends Condition {

    public static final String REF_PREFIX = "taskFailed|";

    public TaskFailedCondition() {
    }

    public static boolean isTaskFailureCondition( String conditionRef ) {
        return conditionRef.startsWith( REF_PREFIX );
    }

    @Override
    public boolean isEventTimingCondition() {
        return false;
    }

    @Override
    public boolean isGoalCondition() {
        return false;
    }

    @Override
    public boolean isNeedSatisfiedCondition() {
        return false;
    }

    @Override
    public boolean isLocalCondition() {
        return false;
    }

    @Override
    public boolean isTaskFailedCondition() {
        return true;
    }

    @Override
    public boolean isAssetAvailableCondition() {
        return false;
    }

    @Override
    public boolean matches( Outcome outcome ) {
        return false;
    }

    @Override
    public String getLabel() {
        return "The task failed";
    }

    @Override
    public String getRef() {
        return REF_PREFIX;
    }

    @Override
    public int hashCode() {
        return REF_PREFIX.hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof TaskFailedCondition;
    }

}
