package com.mindalliance.channels.core.model;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * A phase and a rated type of event.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/11/11
 * Time: 9:35 AM
 */
public class EventPhase implements Serializable, Comparable<EventPhase> {

    /** A type of event. */
    private Event event;

    /** A phase of the event. */
    private Phase phase;

    /** A rating of the event. */
    private Level eventLevel;

    public EventPhase() {
    }

    public EventPhase( Event event, Phase phase, Level eventLevel ) {
        assert event != null && phase != null;
        this.event = event;
        this.phase = phase;
        this.eventLevel = eventLevel;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent( Event event ) {
        this.event = event;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase( Phase phase ) {
        this.phase = phase;
    }

    public Level getEventLevel() {
        return eventLevel;
    }

    public void setEventLevel( Level eventLevel ) {
        this.eventLevel = eventLevel;
    }

    public void initFrom( EventPhase eventPhase ) {
        setEvent( eventPhase.getEvent() );
        setPhase( eventPhase.getPhase() );
        setEventLevel( eventPhase.getEventLevel() );
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( StringUtils.capitalize( phase.getName() ) );
        sb.append( ' ' );
        sb.append( phase.getPreposition() );
        sb.append( ' ' );
//        sb.append( ChannelsUtils.smartUncapitalize( event.getName() ) );
        sb.append( event.getName() );
        if ( eventLevel != null ) {
            sb.append( " (" );
            sb.append( eventLevel.getLabel().toLowerCase() );
            sb.append( ')' );
        }
        return sb.toString();
    }

    @Override
    public int compareTo( EventPhase o ) {
        if ( o == null )
            return 1;
        int a1 = o.getPhase() == null ? 1 : phase.compareTo( o.getPhase() );
        if ( a1 == 0 ) {
            int a2 = o.getEvent() == null ? 1 : event.compareTo( o.getEvent() );
            return a2 != 0 ? a2
                 : o.getEventLevel() == null ? 1
                 : eventLevel.compareTo( o.getEventLevel() );
        }
        return a1;
    }

    /**
     * Whether references a given model object.
     *
     * @param mo a model object
     * @return a boolean
     */
    public boolean references( ModelObject mo ) {
        return ModelObject.areIdentical( phase, mo ) || ModelObject.areIdentical( event, mo );
    }

    public boolean narrowsOrEquals( EventPhase other, Place locale ) {
        return phase.equals( other.getPhase() ) && event.narrowsOrEquals( other.getEvent(), locale )
               && Level.isSubsumedBy( eventLevel, other.getEventLevel() );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o )
            return true;

        if ( o == null || getClass() != o.getClass() )
            return false;

        EventPhase that = (EventPhase) o;

        if ( event == null ? that.event != null : !event.equals( that.event ) )
            return false;

        return eventLevel == that.eventLevel
            && ( phase == null ? that.getPhase() == null : phase.equals( that.getPhase() ) );
    }

    @Override
    public int hashCode() {
        int result = event != null ? event.hashCode() : 0;
        result = 31 * result + ( phase != null ? phase.hashCode() : 0 );
        result = 31 * result + ( eventLevel != null ? eventLevel.hashCode() : 0 );
        return result;
    }

    public EventTiming getEventTiming() {
        return new EventTiming( getPhase().getTiming(), getEvent() );
    }
}
