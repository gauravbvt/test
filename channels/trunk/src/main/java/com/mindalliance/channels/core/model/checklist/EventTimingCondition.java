package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Place;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/13
 * Time: 11:51 PM
 */
public class EventTimingCondition extends Condition {

    public static final String REF_PREFIX = "et|";

    private EventTiming eventTiming;

    public EventTimingCondition( EventTiming eventTiming ) {
        this.eventTiming = eventTiming;
    }

    public EventTiming getEventTiming() {
        return eventTiming;
    }

    @Override
    public String getRef() {
        return REF_PREFIX + eventTiming.getTiming().name() + "|" + eventTiming.getEvent().getId();
    }

    @Override
    public boolean isEventTimingCondition() {
        return true;
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
    public boolean matches( Outcome outcome ) {
        if ( outcome.isEventTimingOutcome() ) {
            EventTimingOutcome eventTimingOutcome = (EventTimingOutcome)outcome;
            return eventTimingOutcome.getEventTiming().narrowsOrEquals( eventTiming, Place.UNKNOWN );
        }
        return false;
    }

    @Override
    public boolean isNeedSatisfiedCondition() {
        return false;
    }

    @Override
    public String getLabel() {
        return getEventTiming().getStepConditionLabel();
    }

    public static boolean isEventTimingRef( String conditionRef ) {
        return conditionRef.startsWith( REF_PREFIX );
    }

    @Override
    public int hashCode() {
        return eventTiming.hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof EventTimingCondition
                && eventTiming.equals( ((EventTimingCondition)object).getEventTiming() );
    }
}
