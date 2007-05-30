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
import java.util.TimeZone;
import java.util.TreeSet;

import org.zkforge.timeline.Bandinfo;
import org.zkforge.timeline.Hotzone;
import org.zkforge.timeline.Timeline;
import org.zkforge.timeline.data.OccurEvent;
import org.zkforge.timeline.event.SelectEvent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.EventListener;

import com.mindalliance.channels.data.components.Cause;
import com.mindalliance.channels.data.components.Caused;
import com.mindalliance.channels.data.elements.Occurrence;
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

    /**
     * Minimum duration of a timeline, in milliseconds.
     */
    private static final int MIN_DURATION = 15000;

    private static final float TOP_TRACK_HEIGHT = 1.4f;
    private static final float BOTTOM_TRACK_HEIGHT = 0.4f;
    private static final float TRACK_GAP = 0.1f;
    private static final int BOTTOM_MARGINS = 5;

    /**
     * The size of a tick in a band.
     * This affect the zoom level.
     */
    private static final int INTERVAL = 30;

    /**
     * Allowed scales for the timelines.
     * @todo Scales need to be tested for practicality and looks
     */
    private enum Scale {
        MS10  ( 10, 10,  "millisecond", 1,   "millisecond" ),
        MS500 ( 50, 50,  "millisecond", 10,  "millisecond" ),
        S     ( 2,  100, "millisecond", 20,  "millisecond" ),
        S10   ( 10, 1,   "second",      100, "millisecond" ),
        S30   ( 3,  5,   "second",      500, "millisecond" ),
        M     ( 2,  10,  "second",      1,   "second" ),
        M5    ( 5,  1,   "minute",      5,   "second" ),
        M15   ( 3,  1,   "minute",      20,  "second" ),
        M30   ( 2,  5,   "minute",      1,   "minute" ),
        H     ( 2,  15,  "minute",      5,   "minute" ),
        H6    ( 6,  1,   "hour",        10,  "minute" ),
        H12   ( 2,  2,   "hour",        30,  "minute" ),
        D     ( 2,  3,   "hour",        45,  "minute" ),
        W     ( 7,  1,   "day",         6,   "hour" ),
        W4    ( 4,  1,   "week",        1,   "day" );

        private int scale;
        private String bottomUnit;
        private int bottomMultiple;
        private String topUnit;
        private int topMultiple;

        private Scale(
                int scale, int bottomMultiple, String bottomUnit,
                int topMultiple, String topUnit ) {

            this.scale = scale;
            this.bottomMultiple = bottomMultiple;
            this.bottomUnit = bottomUnit;
            this.topMultiple = topMultiple;
            this.topUnit = topUnit;
        }

        int bottomInterval() {
            return Math.max( 1, INTERVAL / bottomMultiple );
        }

        int topInterval() {
            return Math.max( 1, INTERVAL / topMultiple );
        }

        long milliseconds() {
            long result = 1;
            for ( int i = 0 ; i <= ordinal() ; i++ )
                result *= values()[ i ].scale ;
            return result;
        }
    }

    private Scenario scenario;
    private Caused selectedObject;
    private List<TimelineListener> selectionListeners;
    private Map<String,Caused> idMap;
    private boolean initialized;
    private IconManager iconManager;
    private Set<ResolvedEvent> events;

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
        this.events = getResolvedEvents( new Date( 0 ) );
        setPage( page );

        Date middle = getMiddleDate();
        TimeZone tz = TimeZone.getTimeZone( "UTC" );
        Bandinfo top = createTop( middle, tz );
        Bandinfo bottom = createBottom( middle, tz, top.getId() );

        // The following sets the *heights* of the tracks
        top.setWidth( "70%" );
        bottom.setWidth( "30%" );

        appendChild( top );
        appendChild( bottom );

        setHeight( height + "px" );
        setWidth( null );
        setSclass( "timeline" );

        // Set initial selection to first object in the timeline.
//        if ( events.size() > 0 )
//            setSelectedObject( events.iterator().next().getObject() );

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
                    populateTimeline();
                }
            }
        } );
    }

    /**
     * Create the top band.
     * @param middle the middle date
     * @param tz the time zome
     */
    private Bandinfo createTop( Date middle, TimeZone tz ) {
        Scale slice = getScale();
        long ms = slice.milliseconds();

        Hotzone hz = new Hotzone();
        final long t = getStartDate().getTime();
        hz.setStart( new Date( t - ms ) );
        hz.setEnd( new Date( getEndDate().getTime() + ms ) );
        hz.setUnit( slice.topUnit );
        hz.setMagnify( 1 );
        hz.setMultiple( slice.topMultiple );

        Bandinfo top = new Bandinfo();
        top.setTimeZone( tz );
        top.setDate( middle );
        top.setTrackHeight( TOP_TRACK_HEIGHT );
        top.setTrackGap( TRACK_GAP );
        top.setIntervalUnit( slice.topUnit );
        top.setIntervalPixels( slice.topInterval() );
        top.appendChild( hz );
        return top;
    }

    /**
     * Create the bottom band.
     * @param middle the middle date
     * @param tz the timezone
     * @param id the id of the top pane to synchronize with.
     */
    private Bandinfo createBottom( Date middle, TimeZone tz, String id ) {
        Scale slice = getScale();
        long ms = BOTTOM_MARGINS * slice.milliseconds();

        Hotzone hz = new Hotzone();
        final long t = getStartDate().getTime();
        hz.setStart( new Date( t - ms ) );
        hz.setEnd( new Date( t + ms ) );
        hz.setUnit( slice.bottomUnit );
        hz.setMagnify( 1 );
        hz.setMultiple( slice.bottomMultiple );

        final Bandinfo bottom = new Bandinfo();
        bottom.setTimeZone( tz );
        bottom.setDate( middle );
        bottom.setIntervalUnit( slice.bottomUnit );
        bottom.setIntervalPixels( slice.bottomInterval() );
        bottom.setTrackHeight( BOTTOM_TRACK_HEIGHT );
        bottom.setTrackGap( TRACK_GAP );
        bottom.setShowEventText( false );
        bottom.setSyncWith( id );
        bottom.appendChild( hz );

        return bottom;
    }

    /**
     * Return a scale that holds the entire scenario.
     */
    private Scale getScale() {
        long ms = getEndDate().getTime() - getStartDate().getTime();
        final Scale[] scales = Scale.values();
        for ( Scale s : scales ) {
            long milliseconds = s.milliseconds();
            if ( ms < milliseconds )
                return s;
        }

        return scales[ scales.length - 1 ];
    }

    /**
     * Return the middle date of the scenario.
     */
    private Date getMiddleDate() {
        final long stime = getStartDate().getTime();
        final long etime = getEndDate().getTime();
        return new Date( stime + ( etime - stime ) / 2 );
    }

    /**
     * Return the start date of the scenario.
     */
    private Date getStartDate() {
        Date result = null ;
        if ( events != null && events.size() > 0 )
            for ( ResolvedEvent e : events )
                if ( result == null || result.after( e.getStart() ) )
                    result = e.getStart();

        return result == null ?
                new Date( 0 )
              : result ;
    }

    /**
     * Return the end date of the scenario.
     */
    private Date getEndDate() {
        Date result = new Date( getStartDate().getTime() + MIN_DURATION ) ;
        if ( events != null && events.size() > 0 )
            for ( ResolvedEvent e : events )
                if ( result.before( e.getEnd() ) )
                    result = e.getEnd();

        return result ;
    }

    /**
     * Put the events in the timeline.
     */
    void populateTimeline() {
        this.idMap = new HashMap<String,Caused>();

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
     * Force a full redraw of the timeline.
     */
    @Override
    public void invalidate() {
        this.initialized = false;
        this.events = getResolvedEvents( new Date( 0 ) );
        super.invalidate();
    }

    /**
     * Resolve the occurrences/products in the scenario starting at
     * a given date.
     * @param start the starting date
     */
    private Set<ResolvedEvent> getResolvedEvents( Date start ) {
        Set<ResolvedEvent> events = new TreeSet<ResolvedEvent>();

        if ( getScenario() != null ) {
            if ( getScenario().getOccurrences() != null )
                for ( Occurrence t : getScenario().getOccurrences() )
                    events.add( new ResolvedEvent( t, start ) );
            if ( getScenario().getProducts() != null )
                for ( Product t : getScenario().getProducts() )
                    events.add( new ResolvedEvent( t, start ) );
        }

        return events;
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
