package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.Event;

/**
 * Event caused by a step in a checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/23/13
 * Time: 11:04 AM
 */
public class EventOutcome extends Outcome {

    public static final String REF_PREFIX = "event|";

    private Event event;

    public EventOutcome( Event event ) {
        this.event = event;
    }

    public static boolean isEventOutcomeRef( String outcomeRef ) {
        return outcomeRef.startsWith( REF_PREFIX );
    }

    public Event getEvent() {
        return event;
    }

    @Override
    public boolean isCapabilityOutcome() {
        return false;
    }

    @Override
    public boolean isEventOutcome() {
        return true;
    }

    @Override
    public boolean isGoalAchievedOutcome() {
        return false;
    }

    @Override
    public String getLabel() {
        return getEvent().getStepOutcomeLabel();
    }

    @Override
    public String getRef() {
        return REF_PREFIX + event.getId();
    }

    @Override
    public int hashCode() {
        return event.hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof EventOutcome
                && event.equals( ((EventOutcome)object).getEvent() );
    }

}
