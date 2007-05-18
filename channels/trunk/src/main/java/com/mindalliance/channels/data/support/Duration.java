// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.support;

import java.io.Serializable;

/**
 * A duration in time.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision: 103 $
 * @opt attributes
 */
public class Duration implements Serializable, Comparable<Duration> {

    /**
     * A unit of time. Default is msecs.
     */
    public enum Unit {
        day, hour, minute, second, msec
    }

    public static final Duration NONE = new Duration();

    private double number;
    private Unit unit;

    /**
     * Default constructor.
     */
    public Duration() {
        this.number = 0;
        unit = Unit.msec;
    }

    /**
     * Default constructor.
     * @param number a number
     * @param unit the unit
     */
    public Duration( double number, Unit unit ) {
        this.number = number;
        this.unit = unit;
    }

    /**
     * Return the value of number.
     */
    public double getNumber() {
        return this.number;
    }

    /**
     * Set the value of number.
     * @param number The new value of number
     */
    public void setNumber( double number ) {
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
     * Get the duration in milliseconds.
     */
    public long getMsecs() {
        long factor;
        switch ( unit ) {
            case second:
                factor = 1000;
                break;
            case minute:
                factor = 60 * 1000;
                break;
            case hour:
                factor = 60 * 60 * 1000;
                break;
            case day:
                factor = 24 * 60 * 60 * 1000;
                break;
            default:
                factor = 1;
        }

        return (long) ( number * factor );
    }

    /**
     * Add a duration to this one.
     * @param duration the duration to add
     * @return a new duration
     */
    public Duration add( Duration duration ) {
        return new Duration( getMsecs() + duration.getMsecs(), Unit.msec );
    }

    /**
     * Compare with another duration.
     * @param other the duration
     */
    public int compareTo( Duration other ) {
        double msecs = getMsecs();
        double otherMsecs = other.getMsecs();
        return msecs < otherMsecs ? -1
             : msecs > otherMsecs ?  1
             : 0;
    }
}
