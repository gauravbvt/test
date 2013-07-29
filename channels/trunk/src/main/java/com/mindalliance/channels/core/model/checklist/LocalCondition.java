package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.Mappable;

import java.util.Map;

/**
 * Condition local to the checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/13
 * Time: 11:50 PM
 */
public class LocalCondition extends Condition implements Mappable {

    public static final String REF_PREFIX = "condition|";
    private String state = "";

    public LocalCondition() {}

    public LocalCondition( String state ) {
        this.state = state;
    }

    public String getState() {
        return state == null ? "" : state;
    }

    public void setState( String state ) {
        this.state = state;
    }

    public boolean isEmpty() {
        return getState().isEmpty();
    }

    public String getRef() {
        return REF_PREFIX + state;
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
    public boolean isLocalCondition() {
        return true;
    }

    @Override
    public boolean isTaskFailedCondition() {
        return false;
    }

    @Override
    public boolean matches( Outcome outcome ) {
        return false;
    }

    @Override
    public boolean isNeedSatisfiedCondition() {
        return false;
    }

    @Override
    public String getLabel() {
        return state;
    }

    public static boolean isLocalConditionRef( String conditionRef ) {
        return conditionRef.startsWith( REF_PREFIX );
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof LocalCondition
                && state.equals( ( (LocalCondition) object ).getState() );
    }

    @Override
    public void map( Map<String, Object> map ) {
        map.put( "state", state );
    }
}
