package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.InfoCapability;
import com.mindalliance.channels.core.model.InfoNeed;
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

    private InfoNeed infoNeed;

    public NeedSatisfiedCondition( InfoNeed infoNeed ) {
        this.infoNeed = infoNeed;
    }

    public Information getNeededInfo() {
        return infoNeed.getInformation();
    }

    public InfoNeed getInfoNeed() {
        return infoNeed;
    }

    @Override
    public String getRef() {
        return REF_PREFIX
                +  getNeededInfo()
                + "|"
                + infoNeed.getSourceSpec();
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
    public boolean isTaskFailedCondition() {
        return false;
    }

    @Override
    public boolean isAssetAvailableCondition() {
        return false;
    }

    @Override
    public boolean matches( Outcome outcome ) {
        // meaningful when a step produces info needed needed by another in the same checklist
        if ( outcome.isCapabilityCreatedOutcome() ) {
           CapabilityCreatedOutcome capabilityCreatedOutcome = (CapabilityCreatedOutcome)outcome;
            InfoCapability capability = capabilityCreatedOutcome.getInfoCapability();
            return capability.getInformation().narrowsOrEquals( infoNeed.getInformation() );
        } else {
            return false;
        }
    }

    @Override
    public String getLabel() {
        return getInfoNeed().getStepConditionLabel();
    }

    public static boolean isNeedRef( String conditionRef ) {
        return conditionRef.startsWith( REF_PREFIX );
    }

    @Override
    public int hashCode() {
        return infoNeed.hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof NeedSatisfiedCondition
                && infoNeed.equals( ((NeedSatisfiedCondition)object).getInfoNeed() );
    }

}
