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
public class Duration implements Serializable, Comparable {

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
     * 
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
     * 
     * @param unit The new value of unit
     */
    public void setUnit( Unit unit ) {
        this.unit = unit;
    }

    /**
     * Get duration in milliseconds
     * 
     * @return
     */
    public double getMsecs() {
        switch ( unit ) {
        case msec:
            return number;
        case second:
            return number * 1000;
        case minute:
            return number * 60 * 1000;
        case hour:
            return number * 60 * 60 * 1000;
        case day:
            return number * 24 * 60 * 60 * 1000;
        default:
            return number;
        }

    }

    public Duration add( Duration duration ) {
        return new Duration( getMsecs() + duration.getMsecs(), Unit.msec );
    }

    public int compareTo( Object obj ) {
        double msecs = getMsecs();
        double otherMsecs = ( (Duration) obj ).getMsecs();
        if ( msecs < otherMsecs )
            return -1;
        if ( msecs > otherMsecs )
            return 1;
        return 0;
    }
}
