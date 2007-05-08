// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.util;

import java.io.Serializable;

/**
 * A duration in time.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 */
public class Duration implements Serializable, Comparable<Duration> {

    /**
     * A unit of time.
     */
    public enum Unit {
        /** 365.25 days. */
        years,

        /** 30 days. */
        months, weeks, days, hours, minutes, seconds, milliseconds
    }

    private static final long[] VALUES = new long[] {
        31557600000L, 2592000000L, 604800000,
        86400000, 3600000, 60000, 1000, 1
    };

    private int number;
    private Unit unit;

    /**
     * Default constructor.
     */
    public Duration() {
    }

    /**
     * Default constructor.
     * @param number the number
     * @param unit the unit
     */
    public Duration( int number, Unit unit ) {
        this();
        this.number = number;
        this.unit = unit;
    }

    /**
     * Return the value of number.
     */
    public int getNumber() {
        return this.number;
    }

    /**
     * Set the value of number.
     * @param number The new value of number
     */
    public void setNumber( int number ) {
        this.number = number;
    }

    /**
     * Return the value of unit.
     */
    public Unit getUnit() {
        return this.unit;
    }

    /**
     * Set the value of unit.
     * @param unit The new value of unit
     */
    public void setUnit( Unit unit ) {
        this.unit = unit;
    }

    /**
     * Get the numbers of milliseconds implied by this duration.
     */
    public long getMilliseconds() {
        return VALUES[ getUnit().ordinal() ] * getNumber();
    }

    /**
     * Compare two durations.
     * @param o another duration
     */
    public int compareTo( Duration o ) {
        long mine = getMilliseconds();
        long others = o.getMilliseconds();
        return mine > others ? 1
             : mine < others ? -1
             : 0 ;
    }

    /**
     * Test if two durations are equal (if the equivalent
     * milliseconds matches, regardless of units).
     * @param obj the thing to compare to
     */
    @Override
    public boolean equals( Object obj ) {
        return this == obj
            || ( obj instanceof Duration
                    && getMilliseconds() == ( (Duration) obj ).getMilliseconds()
                );
    }

    /**
     * Returns a hash code.
     */
    @Override
    public int hashCode() {
        return (int) getMilliseconds();
    }
}
