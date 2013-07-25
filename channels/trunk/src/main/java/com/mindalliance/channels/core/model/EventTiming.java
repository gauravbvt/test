package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.model.Phase.Timing;

import java.io.Serializable;

/**
 * Either during or after an event type.
 */
public class EventTiming implements Serializable {

    /**
     * A type of event.
     */
    private Event event;

    /**
     * Optional level of event.
     */
    private Level eventLevel;

    /**
     * Timing relative to event. Either during or after since it is always before a type of event.
     */
    private Timing timing = Timing.Concurrent;

    //-------------------------------
    public EventTiming() {
    }

    public EventTiming( Timing timing, Event event ) {
        this( timing, event, null );
    }

    public EventTiming( Timing timing, Event event, Level eventLevel ) {
        this.timing = timing;
        this.event = event;
        this.eventLevel = eventLevel;
    }

    public EventTiming( EventPhase eventPhase ) {
        this( eventPhase.getPhase().getTiming(), eventPhase.getEvent() );
    }

    //-------------------------------
    public static boolean eventLevelImplied( Level eventLevel, Level other ) {
        return eventLevel == null || other != null && eventLevel.compareTo( other ) <= 0;
    }

    public boolean implies( EventTiming other, Place planLocale ) {
        return timing.equals( other.getTiming() ) && other.getEvent().narrowsOrEquals( event, planLocale )
                && eventLevelImplied( eventLevel, other.getEventLevel() );
    }

    public boolean isConcurrent() {
        return timing == Timing.Concurrent;
    }

    public boolean narrowsOrEquals( EventTiming other, Place locale ) {
        return timing == other.getTiming() && Level.isSubsumedBy( eventLevel, other.getEventLevel() )
                && event.narrowsOrEquals( other.getEvent(), locale );
    }

    /**
     * Whether references a given model object.
     *
     * @param mo a model object
     * @return a boolean
     */
    public boolean references( ModelObject mo ) {
        return ModelObject.areIdentical( event, mo );
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

    public Timing getTiming() {
        return timing;
    }

    public void setTiming( Timing timing ) {
        assert timing != null && timing != Timing.PreEvent;
        this.timing = timing;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof EventTiming ) {
            EventTiming other = (EventTiming) obj;
            return timing == other.getTiming() && eventLevel == other.getEventLevel() && event != null
                    && event.equals( other.getEvent() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        if ( timing != null )
            hash = hash * 31 + timing.hashCode();
        if ( eventLevel != null )
            hash = hash * 31 + eventLevel.hashCode();
        if ( event != null )
            hash = hash * 31 + event.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( timing == Timing.Concurrent ? "while " : "after " );
        sb.append( event == null ? "?" : event.getName() );
        sb.append( eventLevelLabel() );
        return sb.toString();
    }

    private String eventLevelLabel() {
        StringBuilder sb = new StringBuilder();
        if ( eventLevel != null ) {
            sb.append( " (" );
            sb.append( eventLevel.getLabel().toLowerCase() );
            if ( !eventLevel.equals( Level.Highest ) ) {
                sb.append( " or greater" );
            }
            sb.append( ")" );
        }
        return sb.toString();
    }

    public EventTiming getEventTimingAfterThis() {
        return timing == Timing.PreEvent
                ? new EventTiming( Timing.Concurrent, getEvent() )
                : timing == Timing.Concurrent
                ? new EventTiming( Timing.PostEvent, getEvent() )
                : null;
    }

    public String getStepConditionLabel() {
        return ( timing == Timing.Concurrent
                ? ""
                : timing == Timing.PostEvent
                ? "Is over: "
                : "Anticipating: " ) + getEvent();
    }

    public String getLabel() {
        return toString();
    }

    public String getStepOutcomeLabel() {
        assert timing != Timing.PreEvent;
        return ( timing == Timing.Concurrent
                ? "Causes:"
                : "Terminates: ") + getEvent();
    }
}
