package com.mindalliance.channels.core.model.time;

import java.io.Serializable;

/**
* Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: 5/1/14
* Time: 2:41 PM
*/
public class Tranche implements Serializable, Comparable<Tranche> {

    private Cycle cycle;
    private TimeUnit timeUnit = TimeUnit.Day;
    private int index = 1;

    public Tranche( Cycle cycle ) {
        this.cycle = cycle;
    }

    public Tranche( Cycle cycle, TimeUnit timeUnit, int index ) {
        this.cycle = cycle;
        this.timeUnit = timeUnit;
        setIndex( index );
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit( TimeUnit tu ) {
        if ( timeUnit != tu ) {
            timeUnit = tu;
            cycle.resetTranches();
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex( int index ) {
        this.index = index;
    }

    public String getLabel() {
        switch ( timeUnit ) {
            case Month:
                return getMonthLabel( index );
            case Week:
                return getWeekLabel( index );
            case Day:
                return getDayLabel( index );
            case Hour:
                return getHourLabel( index );
            default:
                return Integer.toString( index - 1 );
        }
    }

    private String getMonthLabel( int index ) {
        switch ( index ) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return null;
        }
    }

    private String getWeekLabel( int index ) {
        return "Week " + index;
    }

    private String getDayLabel( int index ) {
        switch ( index ) {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                return null;
        }
    }

    private String getHourLabel( int index ) {
        int hours = index - 1;
        if ( hours == 12 )
            return "12PM";
        else if ( hours > 12 )
            return ( hours - 12 ) + "PM";
        else
            return hours + "AM";
    }

    /// COMPARABLE

    @Override
    public int compareTo( Tranche other ) {
        assert other != null;
        int i = timeUnit.compareTo( other.getTimeUnit() );
        if ( i != 0 )
            return i;
        else {
            return index < other.getIndex()
                    ? -1
                    : index > other.getIndex()
                    ? 1
                    : 0;
        }
    }


    /// OBJECT

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof Tranche ) {
            Tranche other = (Tranche) object;
            return timeUnit == other.getTimeUnit() &&
                    index == other.getIndex();
        } else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash + 31 * timeUnit.hashCode();
        hash = hash + 31 * index;
        return hash;
    }

}
