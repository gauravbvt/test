// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.zkforge.timeline.Bandinfo;
import org.zkforge.timeline.Timeline;
import org.zkforge.timeline.data.OccurEvent;
import org.zkforge.timeline.event.SelectEvent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.EventListener;

import com.mindalliance.channels.model.Occurence;
import com.mindalliance.channels.model.Occurence.RelativeTimePoint;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Task;

/**
 * A timeline view of a scenario. Displays when events/tasks/etc... in
 * a particular scenario occur. Selecting elements in this view will
 * trigger a selection event.
 *
 * @author dfeeney
 * @version $Revision$
 */
public class ScenarioTimeline extends Timeline {

    private static final float TOP_TRACK_HEIGHT = 1.4f;
    private static final float BOTTOM_TRACK_HEIGHT = 0.4f;
    private static final int TOP_INTERVAL = 20;
    private static final float TRACK_GAP = 0.1f;

    private Scenario scenario;
    private Occurence selectedOccurence;
    private List<TimelineListener> selectionListeners;
    private Map<String,Occurence> idMap;

    /**
     * Default constructor.
     * @param height the available height in pixels
     * @param page the page
     * @param scenario the scenario
     */
    public ScenarioTimeline( int height,  Page page, Scenario scenario ) {
        super();
        this.scenario = scenario;
        setPage( page );

        TimeZone timeZone = TimeZone.getTimeZone( "EDT" );
        Date start = new Date();

        final Bandinfo top = new Bandinfo();
        top.setTimeZone( timeZone );
        top.setDate( start );
        top.setTrackHeight( TOP_TRACK_HEIGHT );
        top.setTrackGap( TRACK_GAP );
        top.setIntervalUnit( "minute" );
        top.setIntervalPixels( TOP_INTERVAL );

        final Bandinfo bottom = new Bandinfo();
        bottom.setTimeZone( timeZone );
        bottom.setDate( start );
        bottom.setIntervalUnit( "hour" );
        bottom.setTrackHeight( BOTTOM_TRACK_HEIGHT );
        bottom.setTrackGap( TRACK_GAP );
        bottom.setShowEventText( false );
        bottom.setSyncWith( top.getId() );

        // The following sets the *heights* of the tracks
        top.setWidth( "70%" );
        bottom.setWidth( "30%" );

        appendChild( top );
        appendChild( bottom );
        setHeight( height + "px" );
        setWidth( null );
        setSclass( "timeline" );
        addEventListener( "onSelectEvent", new EventListener() {
            public boolean isAsap() {
                return false;
            }

            public void onEvent( org.zkoss.zk.ui.event.Event event ) {
                SelectEvent se = (SelectEvent) event;
                String id = se.getIds()[0];
                java.lang.System.err.println( "id == " + id );
                setSelectedOccurence( idMap.get( id ) );
            }
        } );

        populateTimeline( new Date() );
    }

    /**
     * Put some events in that timeline.
     * @param start the origin of the timeline
     */
    void populateTimeline( Date start ) {
        this.idMap = new HashMap<String,Occurence>();

        List<ResolvedEvent> events = new ArrayList<ResolvedEvent>();

        if ( getScenario() != null && getScenario().getOccurences() != null )
            for ( Occurence event : getScenario().getOccurences() )
                events.add( new ResolvedEvent( event, start ) );

        for ( ResolvedEvent event : events ) {
            OccurEvent occurence = new OccurEvent();
            occurence.setText( event.getName() );
            occurence.setDescription( event.getDescription() );
            occurence.setIconUrl( event.getIcon() );
            occurence.setStart( event.getStart() );
            occurence.setDuration( event.isDuration() );
            if ( event.isDuration() )
                occurence.setEnd( event.getEnd() );

            idMap.put( occurence.getId(), event.getOccurence() );
            addOccurEvent( occurence );
        }
    }

    /**
     * Return the value of scenario.
     */
    public final Scenario getScenario() {
        return this.scenario;
    }

    /**
     * Return the value of selection.
     */
    public Occurence getSelectedOccurence() {
        return this.selectedOccurence;
    }

    /**
     * Set the value of selection.
     * @param selection The new value of selection
     */
    public void setSelectedOccurence( Occurence selection ) {
        Occurence old = this.selectedOccurence;
        this.selectedOccurence = selection;
        fireSelectionChanged( old, selection );
    }

    private synchronized List<TimelineListener> getSelectionListeners() {
        if ( selectionListeners == null )
            selectionListeners = Collections.synchronizedList(
                                    new ArrayList<TimelineListener>() );
        return selectionListeners;
    }

    /**
     * Add a selection listener to this timeline.
     * @param listener the listener
     */
    public void addTimelineListener( TimelineListener listener ) {
        getSelectionListeners().add( listener );
    }

    /**
     * Remove a selection listener from this timeline.
     * @param listener the listener
     */
    public void removeTimelineListener( TimelineListener listener ) {
        if ( selectionListeners != null )
            getSelectionListeners().remove( listener );
    }

    private void fireSelectionChanged(
            Occurence oldSelection, Occurence newSelection ) {
        if ( selectionListeners != null && oldSelection != newSelection ) {
            synchronized ( selectionListeners ) {
                for ( TimelineListener listener : selectionListeners )
                    listener.selectionChanged(
                            this, oldSelection, newSelection );
            }
        }
    }

    //==================================================
    /**
     * Wrapper around an event to resolve actual dates/times to put relative
     * events in a absolute timeline.
     */
    private static class ResolvedEvent  {

        private Occurence occurence;
        private Date origin;
        private Date start;
        private Date end;

        /**
         * Default constructor.
         * @param occurence the original event
         * @param origin a fixed point in time
         */
        public ResolvedEvent( Occurence occurence, Date origin ) {

            if ( occurence == null )
                throw new NullPointerException();

            this.occurence = occurence;
            this.origin = origin;
        }

        /**
         * Return an icon url for this event.
         */
        public String getIcon() {
            // TODO make the icon depend on the actual event type
            return isDuration()? null : "images/16x16/nav_plain_red.png";
        }

        /**
         * Return the label of this event.
         */
        public String getName() {
            return getOccurence().getName();
        }

        /**
         * Return the description for the event.
         */
        public String getDescription() {
            return getOccurence().getAbout();
        }

        /**
         * Return the endpoint for this event.
         */
        public Date getEnd() {
            if ( end == null )
                end = isDuration() ?
                    new Date(
                        getStart().getTime()
                        + getOccurence().getWhen().getDuration()
                            .getMilliseconds()
                    )
                    : getStart();

            return end;
        }

        /**
         * Return the endpoint for this event.
         */
        public Date getStart() {

            if ( start == null ) {
                RelativeTimePoint when = getOccurence().getWhen();
                if ( when != null && when.isRelative() ) {
                    // Relative to another occurence
                    ResolvedEvent another =
                        new ResolvedEvent( when.getTo(), origin );
                    start = another.getEnd();

                } else {
                    // TODO Absolute... Use origin?
                    start = origin;
                }
            }

            return start;
        }

        /**
         * Tells if this event is a duration, as opposed to a
         * single point in time.
         */
        public boolean isDuration() {
            return Task.class.isAssignableFrom( getOccurence().getClass() );
        }

        /**
         * Return the value of occurence.
         */
        public final Occurence getOccurence() {
            return this.occurence;
        }
    }
}
