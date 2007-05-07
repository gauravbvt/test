// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.zkforge.timeline.Bandinfo;
import org.zkforge.timeline.Timeline;
import org.zkforge.timeline.event.SelectEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import com.mindalliance.channels.model.Scenario;

/**
 * A timeline view of a scenario. Displays when events/tasks/etc... in
 * a particular scenario occur. Selecting elements in this view will
 * trigger a selection event.
 *
 * @author dfeeney
 * @version $Revision$
 */
public class ScenarioTimeline extends Timeline {

    private Scenario scenario;
    private Date start = new Date();

    /**
     * Default constructor.
     * @param height the available height in pixels
     * @param scenario the scenario
     */
    public ScenarioTimeline( int height, Scenario scenario ) {
        super();
        this.scenario = scenario;

        TimeZone timeZone = TimeZone.getTimeZone( "EDT" );
        Calendar date = Calendar.getInstance();
        date.setTimeZone( timeZone );
        date.set( 2007, 5, 5, 13, 0 );
        setStart( date.getTime() );

        final Bandinfo top = new Bandinfo();
        top.setTimeZone( timeZone );
        top.setDate( getStart() );
        top.setTrackHeight( 1.0f );
        top.setIntervalUnit( "minute" );
        top.setIntervalPixels( 20 );
        top.setEventSourceUrl( "scenario.jsp" );

        final Bandinfo bottom = new Bandinfo();
        bottom.setTimeZone( timeZone );
        bottom.setDate( getStart() );
        bottom.setIntervalUnit( "hour" );
        bottom.setTrackHeight( 0.4f );
        bottom.setTrackGap( 0.1f );
        bottom.setEventSourceUrl( "scenario.jsp" );
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

            public void onEvent( Event event ) {
                SelectEvent se = (SelectEvent) event;
                se.getData();
            }
        } );

//        populateTimeline( top );
//        populateTimeline( bottom );
    }

//    /**
//     * Put some events in that timeline.
//     */
//    private void populateTimeline( Bandinfo band ) {
//        // TODO figure out how to make something like the following work
//
//        OccurEvent ev1 = new OccurEvent();
//        ev1.setText( "Fire alarm triggered" );
//        ev1.setDescription( "Flee!" );
//        ev1.setIconUrl( "images/16x16/nav_plain_red.png" );
//        ev1.setStart( getStart() );
//        ev1.setDuration( false );
//
//        OccurEvent ev2 = new OccurEvent();
//        ev2.setText( "Verify cause of alarm" );
//        ev2.setDuration( true );
//        ev2.setDescription( "Flee!" );
//        ev2.setIconUrl( "images/16x16/forbidden.png" );
//        Date s = new Date( getStart().getTime() + 60000 );
//        ev2.setStart( s );
//        ev2.setEnd( new Date( s.getTime() + 5*60000 ) );
//
//        band.addManyOccureEvents(
//            new ArrayList<OccurEvent>( Arrays.asList( new OccurEvent[] {
//                ev1, ev2
//            } ) ) );
//    }

    /**
     * Return the value of scenario.
     */
    public final Scenario getScenario() {
        return this.scenario;
    }

    /**
     * Return the value of start.
     */
    public final Date getStart() {
        return this.start;
    }

    /**
     * Set the value of start.
     * @param start The new value of start
     */
    public void setStart( Date start ) {
        this.start = start;
    }
}
