package com.mindalliance.channels.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Time-based availability.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2010
 * Time: 1:20:31 PM
 */
public class Availability implements Serializable {

    private List<TimePeriod> timePeriods;

    public Availability() {
        // default is 24/7
        this( 0, 1440 );
    }

    public Availability( int fromTime, int toTime ) {
        timePeriods = new ArrayList<TimePeriod>();
        for (int i=0; i < 7; i++)
            timePeriods.add( new TimePeriod( fromTime, toTime ) );
    }

    public static String dayOfWeek( int dayIndex ) {
        switch (dayIndex) {
            case 0: return "Sunday";
            case 1: return "Monday";
            case 2: return "Tuesday";
            case 3: return "Wednesday";
            case 4: return "Thursday";
            case 5: return "Friday";
            case 6: return "Saturday";
            default: throw new IllegalArgumentException( "Unknown day");
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder( );
        if ( isAlways() ) {
            sb.append( "Always" );
        } else {
            for ( int i = 0; i < 7; i++ ) {
                TimePeriod timePeriod = timePeriods.get( i );
                sb.append( dayOfWeek( i) );
                sb.append( ": " );
                sb.append(  timePeriod.toString() );
                if ( i < 6 ) sb.append( ", " );
            }
        }
        return sb.toString();
    }

    public List<TimePeriod> getTimePeriods() {
        return timePeriods;
    }

    public void setTimePeriods( List<TimePeriod> timePeriods ) {
        this.timePeriods = timePeriods;
    }

    public void setTimePeriod( int dayOfWeek, TimePeriod timePeriod ) {
        timePeriods.set( dayOfWeek, timePeriod );
    }

    public TimePeriod getTimePeriod( int dayOfWeek ) {
        return timePeriods.get( dayOfWeek );
    }

    public boolean includes( Date date ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( date );
        int dayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );
        TimePeriod timePeriod = timePeriods.get( dayOfWeek );
        return timePeriod.contains( calendar );
    }

    public Availability computeOverlap( Availability other ) {
        Availability overlap = new Availability();
        for ( int i = 0; i < 7; i++ ) {
            overlap.setTimePeriod( i, getTimePeriod( i ).overlap( other.getTimePeriod( i ) ) );
        }
        return overlap;
    }

    public boolean includes( Availability other ) {
        for ( int i = 0; i <7; i++ ) {
            if ( !getTimePeriod( i ).includesTimePeriod( other.getTimePeriod( i ) ) )
                return false;
        }
        return true;
    }

    public boolean isEmpty() {
        for ( TimePeriod timePeriod : timePeriods) {
            if ( !( timePeriod.isEmpty() || timePeriod.isNil() ) )
                return false;
        }
        return true;
    }

    public boolean isAlways() {
        boolean always = true;
        for ( TimePeriod timePeriod : timePeriods) {
           if ( !timePeriod.isAllDay() ) {
               always = false;
               break;
           }
        }
        return always;
    }

    public boolean equals( Object other ) {
        return other instanceof Availability &&
                toString().equals( other.toString() );
    }

    public int hashCode() {
        return toString().hashCode();
    }

}
