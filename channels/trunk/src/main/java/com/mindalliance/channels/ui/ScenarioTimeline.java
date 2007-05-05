// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.util.Date;
import java.util.TimeZone;

import org.zkforge.timeline.Bandinfo;
import org.zkforge.timeline.Timeline;
import org.zkforge.timeline.data.OccurEvent;

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

    private static final float TRACK_HEIGHT = 0.5f;
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

        TimeZone timeZone = TimeZone.getTimeZone( "GMT-04" );

        final Bandinfo b1 = new Bandinfo();
        b1.setDate( getStart() );
        b1.setTimeZone( timeZone );
        b1.setIntervalUnit( "minute" );
        b1.setTrackHeight( TRACK_HEIGHT );

        final Bandinfo b2 = new Bandinfo();
        b2.setDate( getStart() );
        b2.setTimeZone( timeZone );
        b2.setIntervalUnit( "hour" );
        b2.setTrackHeight( TRACK_HEIGHT );
        b2.setSyncWith( b1.getId() );
        b2.setShowEventText( false );

        // The following sets the *heights* of the tracks
        b1.setWidth( "70%" );
        b2.setWidth( "30%" );

        appendChild( b1 );
        appendChild( b2 );
        setHeight( height + "px" );
        setWidth( null );
        setSclass( "timeline" );

        // TODO Initialize or or both?
        populateTimeline( b1 );
        populateTimeline( b2 );
    }

    /**
     * Put some events in that timeline.
     */
    private void populateTimeline( Bandinfo band ) {
        // TODO figure out how to make something like the following work

        OccurEvent ev1 = new OccurEvent();
        ev1.setText( "Fire alarm triggered" );
        ev1.setDescription( "Flee!" );
        ev1.setIconUrl( "images/16x16/nav_plain_red.png" );
        ev1.setStart( getStart() );
        ev1.setDuration( false );

        band.addOccurEvent( ev1 );
    }

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
