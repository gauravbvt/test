package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.Information;

/**
 * Info need satisfaction condition.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/22/13
 * Time: 2:27 PM
 */
public class NeedSatisfiedCondition extends Condition {

    public static final String REF_PREFIX = "need|";

    private Information neededInfo;

    public NeedSatisfiedCondition( Information neededInfo ) {
        this.neededInfo = neededInfo;
    }

    public Information getNeededInfo() {
        return neededInfo;
    }

    @Override
    public String getRef() {
        return REF_PREFIX +  neededInfo.toString();
    }

    @Override
    public boolean isNeedSatisfiedCondition() {
        return true;
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
        return false;
    }

    @Override
    public String getLabel() {
        return getNeededInfo().getStepConditionLabel();
    }

    public static boolean isNeedRef( String conditionRef ) {
        return conditionRef.startsWith( REF_PREFIX );
    }

    @Override
    public int hashCode() {
        return neededInfo.hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof NeedSatisfiedCondition
                && neededInfo.equals( ((NeedSatisfiedCondition)object).getNeededInfo() );
    }

}
