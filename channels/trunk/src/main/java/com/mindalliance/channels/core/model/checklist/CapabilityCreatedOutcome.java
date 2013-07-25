package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.Information;

/**
 * Info sharing capability that ought to be created by executing a step in a checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/23/13
 * Time: 11:05 AM
 */
public class CapabilityCreatedOutcome extends Outcome {

    public static final String REF_PREFIX = "capability|";

    private Information capability;

    public CapabilityCreatedOutcome( Information capability ) {
        this.capability = capability;
    }

    public static boolean isCapabilityCreatedOutcomeRef( String outcomeRef ) {
        return outcomeRef.startsWith( REF_PREFIX );
    }

    public Information getCapability() {
        return capability;
    }

    @Override
    public boolean isCapabilityCreatedOutcome() {
        return true;
    }

    @Override
    public boolean isEventTimingOutcome() {
        return false;
    }

    @Override
    public boolean isGoalAchievedOutcome() {
        return false;
    }

    @Override
    public String getLabel() {
        return getCapability().getStepOutcomeLabel();
    }

    @Override
    public String getRef() {
        return REF_PREFIX +  capability.toString();
    }

    @Override
    public int hashCode() {
        return capability.hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof CapabilityCreatedOutcome
                && capability.equals( ((CapabilityCreatedOutcome)object).getCapability() );
    }

}
