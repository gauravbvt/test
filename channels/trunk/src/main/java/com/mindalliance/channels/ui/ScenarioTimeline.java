// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.zkforge.timeline.Bandinfo;
import org.zkforge.timeline.Timeline;
import org.zkforge.timeline.data.OccurEvent;
import org.zkforge.timeline.event.SelectEvent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.EventListener;

import com.mindalliance.channels.data.Caused;
import com.mindalliance.channels.data.Occurrence;
import com.mindalliance.channels.data.components.Cause;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.data.elements.scenario.Product;

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
    private Caused selectedObject;
    private List<TimelineListener> selectionListeners;
    private Map<String,Caused> idMap;

    private boolean initialized;
    private IconManager iconManager;

    /**
     * Default constructor.
     * @param height the available height in pixels
     * @param im the icon manager
     * @param page the page
     * @param scenario the scenario
     */
    public ScenarioTimeline(
            int height, IconManager im, Page page, Scenario scenario ) {

        super();
        this.scenario = scenario;
        this.iconManager = im;
        setPage( page );

//        TimeZone timeZone = TimeZone.getTimeZone( "EDT" );
        final Date start = new Date();

        final Bandinfo top = new Bandinfo();
//        top.setTimeZone( timeZone );
        top.setDate( start );
        top.setTrackHeight( TOP_TRACK_HEIGHT );
        top.setTrackGap( TRACK_GAP );
        top.setIntervalUnit( "minute" );
        top.setIntervalPixels( TOP_INTERVAL );

//        final Hotzone topHz = new Hotzone();
//        topHz.setMagnify( 5 );

        final Bandinfo bottom = new Bandinfo();
//        bottom.setTimeZone( timeZone );
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
                setSelectedObject( idMap.get( id ) );
            }
        } );

        top.addEventListener( "onBandScroll", new EventListener() {
            public boolean isAsap() {
                return true;
            }

            public void onEvent( org.zkoss.zk.ui.event.Event event ) {
                if ( !initialized ) {
                    initialized = true;
                    populateTimeline( start );
                }
            }
        } );
    }

    /**
     * Put some events in that timeline.
     * @param start the origin of the timeline
     */
    void populateTimeline( Date start ) {
        this.idMap = new HashMap<String,Caused>();

        Set<ResolvedEvent> events = new TreeSet<ResolvedEvent>();

        if ( getScenario() != null ) {
            if ( getScenario().getOccurrences() != null )
                for ( Occurrence t : getScenario().getOccurrences() )
                    events.add( new ResolvedEvent( t, start ) );
            if ( getScenario().getProducts() != null )
                for ( Product t : getScenario().getProducts() )
                    events.add( new ResolvedEvent( t, start ) );
        }

        for ( ResolvedEvent event : events ) {
            OccurEvent occurrence = new OccurEvent();
            occurrence.setText( event.getName() );
            occurrence.setDescription( event.getDescription() );
            occurrence.setIconUrl(
                getIconManager().getSmallIcon( event.getObject() ) );
            occurrence.setStart( event.getStart() );
            occurrence.setDuration( event.isDuration() );
            if ( event.isDuration() )
                occurrence.setEnd( event.getEnd() );

            idMap.put( occurrence.getId(), event.getObject() );
            addOccurEvent( occurrence );
        }
    }

    /**
     * Return the value of scenario.
     */
    public final Scenario getScenario() {
        return this.scenario;
    }

    /**
     * Return the value of iconManager.
     */
    public IconManager getIconManager() {
        return this.iconManager;
    }

    /**
     * Return the value of selection.
     */
    public Caused getSelectedObject() {
        return this.selectedObject;
    }

    /**
     * Set the value of selection.
     * @param selection The new value of selection
     */
    public void setSelectedObject( Caused selection ) {
        Caused old = this.selectedObject;
        this.selectedObject = selection;
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
            Caused oldSelection, Caused newSelection ) {
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
    private static class ResolvedEvent implements Comparable<ResolvedEvent> {

        private Caused object;
        private Date origin;
        private Date start;
        private Date end;
        private String name;
        private String description;

        /**
         * Default constructor.
         * @param caused the model object
         * @param origin a fixed point in time
         */
        public ResolvedEvent( Product caused, Date origin ) {

            if ( caused == null )
                throw new NullPointerException();

            this.object = caused;
            this.origin = origin;
            this.description = caused.getDescription();
            this.name = caused.getName();
        }

        /**
         * Default constructor.
         * @param caused the model object
         * @param origin a fixed point in time
         */
        public ResolvedEvent( Occurrence caused, Date origin ) {

            if ( caused == null )
                throw new NullPointerException();

            this.object = caused;
            this.origin = origin;
            this.description = caused.getDescription();
            this.name = caused.getName();
        }

        /**
         * Return the label of this event.
         */
        public String getName() {
            return this.name;
        }

        /**
         * Return the description for the event.
         */
        public String getDescription() {
            return this.description;
        }

        /**
         * Return the endpoint for this event.
         */
        public Date getEnd() {
            if ( end == null )
                end = isDuration() ?
                    new Date( getStart().getTime()
                        + getObject().getDuration().getMsecs() )
                    : getStart();

            return end;
        }

        /**
         * Return the starting point for this event.
         */
        public Date getStart() {
            if ( start == null ) {
                Cause cause = getObject().getCause();
                if ( cause != null ) {
                    // Relative to another occurence
                    Occurrence basis = getObject().getCause().getOccurrence();
                    ResolvedEvent another = new ResolvedEvent( basis, origin );
                    start = another.getEnd();

                } else
                    start = origin;
            }

            return start;
        }

        /**
         * Tells if this event is a duration, as opposed to a
         * single point in time.
         */
        public boolean isDuration() {
            return getObject().getDuration().getMsecs() > 0;
        }

        /**
         * Return the value of occurence.
         */
        public final Caused getObject() {
            return this.object;
        }

        /**
         * Sort by start, then reverse end, then name.
         */
        public int compareTo( ResolvedEvent o ) {
            int result = getStart().compareTo( o.getStart() );
            if ( result == 0 ) {
                result = -1 * getEnd().compareTo( o.getEnd() );
                if ( result == 0 )
                    result = getName().compareTo( o.getName() );
            }
            return result;
        }
    }
}
