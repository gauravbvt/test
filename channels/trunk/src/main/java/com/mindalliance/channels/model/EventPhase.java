package com.mindalliance.channels.model;

import com.mindalliance.channels.util.ChannelsUtils;
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
public class EventPhase implements Serializable {
    /**
     * A type of event.
     */
    private Event event;
    /**
     * A phase of the event.
     */
    private Phase phase;
    /**
     * A rating of the event.
     */
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

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( StringUtils.capitalize( getPhase().getName() ) );
        sb.append( ' ' );
        sb.append( getPhase().getPreposition() );
        sb.append( ' ' );
        sb.append( ChannelsUtils.smartUncapitalize( getEvent().getName() ) );
        if ( getEventLevel() != null ) {
            sb.append( " (" );
            sb.append( getEventLevel().getLabel() );
            sb.append( ')' );
        }
        return sb.toString();
    }

    /**
     * Whether references a given model object.
     * @param mo a model object
     * @return a boolean
     */
    public boolean references( ModelObject mo ) {
        return ModelObject.areIdentical( getPhase(), mo )
                || ModelObject.areIdentical( getEvent(), mo );
    }

    public boolean narrowsOrEquals( EventPhase other, Place locale ) {
        return phase.equals( other.getPhase() )
                && event.narrowsOrEquals( other.getEvent(), locale )
                && Level.isSubsumedBy( eventLevel, other.getEventLevel() );
    }
}
