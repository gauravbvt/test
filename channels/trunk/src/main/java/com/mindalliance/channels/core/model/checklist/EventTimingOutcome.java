package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.EventTiming;

/**
 * Event caused by a step in a checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/23/13
 * Time: 11:04 AM
 */
public class EventTimingOutcome extends Outcome {

    public static final String REF_PREFIX = "event|";

    private EventTiming eventTiming;

    public EventTimingOutcome( EventTiming eventTiming ) {
        this.eventTiming = eventTiming;
    }

    public static boolean isEventOutcomeRef( String outcomeRef ) {
        return outcomeRef.startsWith( REF_PREFIX );
    }

    public EventTiming getEventTiming() {
        return eventTiming;
    }

    @Override
    public boolean isCapabilityCreatedOutcome() {
        return false;
    }

    @Override
    public boolean isEventTimingOutcome() {
        return true;
    }

    @Override
    public boolean isGoalAchievedOutcome() {
        return false;
    }

    @Override
    public String getLabel() {
        return getEventTiming().getStepOutcomeLabel();
    }

    @Override
    public String getRef() {
        return REF_PREFIX + eventTiming.getTiming().name() + "|" + eventTiming.getEvent().getId();
    }

    @Override
    public int hashCode() {
        return eventTiming.hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof EventTimingOutcome
                && eventTiming.equals( ( (EventTimingOutcome) object ).getEventTiming() );
    }

}
