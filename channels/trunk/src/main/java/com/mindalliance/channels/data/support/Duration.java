// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.support;

import java.io.Serializable;

import com.beanview.annotation.PropertyOptions;

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
        /** 7 days. */
        week, day, hour, minute, second, msec
    }

    /**
     * A default duration of zero milliiseconds.
     */
    public static final Duration NONE = new Duration();

    private static final long[] VALUES = new long[] {
        604800000, 86400000, 3600000, 60000, 1000, 1
    };

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
    @PropertyOptions(label=" ")
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
    @PropertyOptions(label=" ")
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
    //@PropertyOptions(ignore=true)
    public long getMsecs() {
        return (long) ( VALUES[ getUnit().ordinal() ] * getNumber() );
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
