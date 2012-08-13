package com.mindalliance.channels.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Contiguous timeperiod in a day.
 * From minutes since midnight to minutes from midnight.
 */
public class TimePeriod implements Serializable {

    int fromTime = 0;
    int toTime = 24 * 60;

    public TimePeriod() {
    }

    public TimePeriod( TimePeriod timePeriod ) {
        fromTime = timePeriod.getFromTime();
        toTime = timePeriod.getToTime();
    }

    public TimePeriod( int fromTime, int toTime ) {
        assert fromTime <= toTime;
        assert fromTime <= 24 * 60;
        assert toTime <= 24 * 60;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public static TimePeriod allDayPeriod() {
        return new TimePeriod( 0, 24 * 60 );
    }

    public static TimePeriod emptyTimePeriod() {
        return new TimePeriod( 0, 0 );
    }

    public static TimePeriod nilTimePeriod() {
        return new TimePeriod( -1, 0 );
    }

    public boolean isAllDay() {
        return fromTime == 0 && toTime == 24 * 60;
    }

    public int getFromTime() {
        return fromTime;
    }

    public void setFromTime( int fromTime ) {
        this.fromTime = Math.min( toTime, fromTime );
    }

    public int getToTime() {
        return toTime;
    }

    public void setToTime( int toTime ) {
        this.toTime = Math.max( fromTime, toTime );
    }

    public boolean includesTimePeriod( TimePeriod other ) {
        return fromTime <= other.getFromTime() && toTime >= other.getToTime();
    }

    public boolean isEmpty() {
        return fromTime == toTime;
    }

    public boolean isNil() {
        return fromTime == -1;
    }

    public boolean contains( Calendar calendar ) {
        int hour = calendar.get( Calendar.HOUR_OF_DAY );
        int minute = calendar.get( Calendar.MINUTE );
        int minutes = ( hour * 60 ) + minute;
        return minutes >= fromTime && minutes <= toTime;
    }

    /**
     * Finds the intersection of two time periods.
     * Returns nil if time periods neither overlap (yields non-nil and non-empty) or contiguous (yields empty)
     *
     * @param other a time period
     * @return a time period (can be nil)
     */
    public TimePeriod overlap( TimePeriod other ) {
        if ( isNil() || other.isNil() ) return nilTimePeriod();
        int maxFromTime = Math.max( fromTime, other.getFromTime() );
        int minToTime = Math.min( toTime, other.getToTime() );
        if ( maxFromTime <= minToTime )
            return new TimePeriod( maxFromTime, minToTime );
        else
            return nilTimePeriod();
    }

    /**
     * Finds a time period between this and another.
     * Returns null if they overlap.
     *
     * @param other a time period
     * @return a time period (can be nil)
     */
    public TimePeriod gap( TimePeriod other ) {
        if ( isNil() || other.isNil() ) return nilTimePeriod();
        if ( toTime < other.getFromTime() )
            return new TimePeriod( toTime, other.getFromTime() );
        else if ( fromTime > other.getToTime() )
            return new TimePeriod( other.getToTime(), fromTime );
        else
            return nilTimePeriod();
    }

    public void setFromHour( int val ) {
        int minutes = getFromMinute();
        setFromTime( ( val * 60 ) + minutes );
    }

    public void setToHour( int val ) {
        int minutes = getToMinute();
        setToTime( ( val * 60 ) + minutes );
    }

    public void setFromMinute( int val ) {
        int hours = getFromHour();
        setFromTime( ( hours * 60 ) + val );
    }

    public void setToMinute( int val ) {
        int hours = getToHour();
        setToTime( ( hours * 60 ) + val );
    }

    public String toString() {
        if ( isAllDay() ) {
            return "All day";
        } else if (isNil() || isEmpty() ) {
            return "Not available";
        }
        else {
                return pad( getFromHour() )
                        + ":" + pad( getFromMinute() )
                        + " to "
                        + pad( getToHour() )
                        + ":" + pad( getToMinute() );
            }
    }

    private String pad( int val ) {
        if ( val >= 10 ) return Integer.toString( val );
        else return "0" + val;
    }

    public int getFromHour() {
        return fromTime / 60;
    }

    public int getFromMinute() {
        return fromTime % 60;
    }

    public int getToHour() {
        return toTime / 60;
    }

    public int getToMinute() {
        return toTime % 60;
    }

    public List<TimePeriod> subtract( TimePeriod period ) {
        List<TimePeriod> result = new ArrayList<TimePeriod>();
        if ( intersects( period ) ) {
            if ( fromTime < period.getFromTime() ) {
                result.add( new TimePeriod( fromTime, period.getFromTime() ) );
            }
            if ( period.getToTime() < toTime ) {
                result.add( new TimePeriod( period.getToTime(), toTime ) );
            }
        } else {
            result.add( new TimePeriod( this ) );
        }
        return result;
    }

    public boolean includes( TimePeriod other ) {
        return !isNil()
                && !other.isNil()
                && fromTime <= other.getFromTime()
                && toTime >= other.getToTime();
    }

    public boolean intersects( TimePeriod other ) {
        return !isNil()
                && !other.isNil()
                && !( other.getFromTime() >= toTime || other.getToTime() <= fromTime );
    }
}
