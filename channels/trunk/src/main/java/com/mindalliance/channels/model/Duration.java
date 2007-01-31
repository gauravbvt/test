// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

/**
 * A duration in time.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Duration {

    /**
     * A unit of time.
     */
    public enum Unit { years, months, weeks, days, hours, minutes, seconds }

    private int number;
    private Unit unit;

    /**
     * Default constructor.
     */
    public Duration() {
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
}
