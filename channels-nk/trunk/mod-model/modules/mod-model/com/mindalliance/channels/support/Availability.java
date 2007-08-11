// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.support;

import java.io.Serializable;

/**
 * A simplistic way of defining availability. Availabilities can be
 * compared.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Availability implements Serializable {

    /**
     * Hours of availability.
     */
    public enum Hours {
        /** Any hour. */
        ALL,

        /** Business hours. */
        BUSINESS_HOURS
    };

    /**
     * Day of availability.
     */
    public enum Days {
        /** Any day. */
        ALL,

        /** Work days. */
        WORK_DAYS
    };

    private Days days = Days.ALL;
    private Hours hours = Hours.ALL;

    /**
     * Default constructor.
     */
    public Availability() {
        super();
    }

    /**
     * Test if a given availability is included in this one.
     * @param availability the availability
     */
    public boolean comprise( Availability availability ) {
        // TODO
        return false;
    }

    /**
     * Test if a given availability overlaps with this one.
     * @param availability the availability
     */
    public boolean overlap( Availability availability ) {
        // TODO
        return false;
    }

    /**
     * Return the days.
     */
    public Days getDays() {
        return days;
    }

    /**
     * Set the days.
     * @param days the days
     */
    public void setDays( Days days ) {
        this.days = days;
    }

    /**
     * Return the hours.
     */
    public Hours getHours() {
        return hours;
    }

    /**
     * Set the hours.
     * @param hours the hours
     */
    public void setHours( Hours hours ) {
        this.hours = hours;
    }

}
