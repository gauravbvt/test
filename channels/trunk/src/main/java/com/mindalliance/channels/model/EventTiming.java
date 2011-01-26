package com.mindalliance.channels.model;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Either during or after an event type.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/11/11
 * Time: 1:53 PM
 */
public class EventTiming implements Serializable {

    /**
     * Timing relative to event.
     * Either during or after since it is always before a type of event.
     */
    private Phase.Timing timing = Phase.Timing.Concurrent;
    /**
     * A type of event.
     */
    private Event event;
    /**
     * Optional level of event.
     */
    private Level eventLevel;

    public EventTiming() {
    }

    public EventTiming( Phase.Timing timing, Event event, Level eventLevel ) {
        this.timing = timing;
        this.event = event;
        this.eventLevel = eventLevel;
    }

    public Phase.Timing getTiming() {
        return timing;
    }

    public void setTiming( Phase.Timing timing ) {
        assert timing != null && timing != Phase.Timing.PreEvent;
        this.timing = timing;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent( Event event ) {
        this.event = event;
    }

    public Level getEventLevel() {
        return eventLevel;
    }

    public void setEventLevel( Level eventLevel ) {
        this.eventLevel = eventLevel;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( timing == Phase.Timing.Concurrent ? "during " : "after " );
        sb.append( getEvent() == null ? "?" : StringUtils.uncapitalize( getEvent().getName() ) );
        sb.append( eventLevelLabel() );
        return sb.toString();
    }

    private String eventLevelLabel() {
        StringBuilder sb = new StringBuilder();
        if ( getEventLevel() != null ) {
            sb.append( " (" );
            sb.append( getEventLevel().getLabel().toLowerCase() );
            if ( !getEventLevel().equals( Level.Highest ) ) {
                sb.append( " or greater");
            }
            sb.append( ")" );
        }
        return sb.toString();
    }

    /**
     * Whether references a given model object.
     *
     * @param mo a model object
     * @return a boolean
     */
    public boolean references( ModelObject mo ) {
        return ModelObject.areIdentical( getEvent(), mo );
    }

    /**
     * {inheritDoc}
     */
    public boolean equals( Object obj ) {
        if ( obj instanceof EventTiming ) {
            EventTiming other = (EventTiming) obj;
            return
                    timing == other.getTiming()
                            && eventLevel ==  other.getEventLevel()
                            && event != null && event.equals( other.getEvent() );
        } else {
            return false;
        }
    }

    /**
     * {inheritDoc}
     */
    public int hashCode() {
        int hash = 1;
        if ( timing != null ) hash = hash * 31 + timing.hashCode();
        if ( eventLevel != null ) hash = hash * 31 + eventLevel.hashCode();
        if ( event != null ) hash = hash * 31 + event.hashCode();
        return hash;
    }


    public boolean implies( EventTiming other, Place planLocale ) {
        return getTiming().equals( other.getTiming() )
                && other.getEvent().narrowsOrEquals( getEvent(), planLocale )
                && ( eventLevelImplied( getEventLevel(), other.getEventLevel() ) );
    }

    public static boolean eventLevelImplied( Level eventLevel, Level other ) {
        return eventLevel == null
                || other != null && eventLevel.compareTo( other ) <= 0;
    }
}
