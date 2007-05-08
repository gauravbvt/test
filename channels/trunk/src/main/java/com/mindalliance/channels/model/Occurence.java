// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.sql.Timestamp;

import com.mindalliance.channels.util.Area;
import com.mindalliance.channels.util.Duration;

/**
 * Something that happens somewhere at some point in time and can cause events.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 * @composed - when 1 TimePoint
 * @composed - until 1 TimePoint
 */
public abstract class Occurence extends ScenarioElement {

    private RelativeTimePoint when;
    private RelativeTimePoint until;
    private Area where;

    /**
     * Default constructor.
     */
    public Occurence() {
        super();
    }

    /**
     * Default constructor.
     * @param scenario the scenario
     */
    public Occurence( Scenario scenario ) {
        super( scenario );
    }

    /**
     * Return the value of until.
     */
    public RelativeTimePoint getUntil() {
        return this.until;
    }

    /**
     * Set the value of until.
     * @param until The new value of until
     */
    public void setUntil( RelativeTimePoint until ) {
        this.until = until;
    }

    /**
     * Return the value of when.
     */
    public RelativeTimePoint getWhen() {
        return this.when;
    }

    /**
     * Set the value of when.
     * @param when The new value of when
     */
    public void setWhen( RelativeTimePoint when ) {
        this.when = when;
    }

    /**
     * Return the value of where.
     */
    public Area getWhere() {
        return this.where;
    }

    /**
     * Set the value of where.
     * @param where The new value of where
     */
    public void setWhere( Area where ) {
        this.where = where;
    }

    //=============================================
    /**
     * A point in time.
     */
    public abstract static class TimePoint {

        private Duration duration = new Duration( 0, Duration.Unit.seconds );

        /**
         * Test if this time point is relative to another event.
         */
        public abstract boolean isRelative();

        /**
         * Return the value of duration.
         */
        public Duration getDuration() {
            return this.duration;
        }

        /**
         * Set the value of duration.
         * @param duration The new value of duration
         */
        public void setDuration( Duration duration ) {
            this.duration = duration;
        }

    }

    //=============================================
    /**
     * A specific point in time.
     *
     * @opt attributes
     */
    public static class AbsoluteTimePoint extends TimePoint {

        private Timestamp time;

        /**
         * Default constructor.
         */
        public AbsoluteTimePoint() {
        }

        /**
         * Convenience constructor.
         * @param time the time
         */
        public AbsoluteTimePoint( Timestamp time ) {
            this();
            setTime( time );
        }

        /**
         * Return the value of time.
         */
        public Timestamp getTime() {
            return this.time;
        }

        /**
         * Set the value of time.
         * @param time The new value of time
         */
        public void setTime( Timestamp time ) {
            this.time = time;
        }

        /**
         * Test if this time point is relative to another event.
         * @return false
         */
        public boolean isRelative() {
            return false;
        }
    }

    //=============================================
    /**
     * A point in time, relative to another occurence.
     *
     * @opt attributes
     * @navassoc - - 1 Occurence
     */
    public static class RelativeTimePoint extends TimePoint {

        /**
         * Relative point of connection with other occurence.
         */
        public enum Boundary { Start, End };

        private Occurence to;
        private Boundary timepoint;

        /**
         * Default constructor.
         */
        public RelativeTimePoint() {
            super();
        }

        /**
         * Return the value of timepoint.
         */
        public Boundary getTimepoint() {
            return this.timepoint;
        }

        /**
         * Set the value of timepoint.
         * @param timepoint The new value of timepoint
         */
        public void setTimepoint( Boundary timepoint ) {
            this.timepoint = timepoint;
        }

        /**
         * Return the value of to.
         */
        public Occurence getTo() {
            return this.to;
        }

        /**
         * Set the value of to.
         * @param to The new value of to
         */
        public void setTo( Occurence to ) {
            this.to = to;
        }

        /**
         * Test if this time point is relative to another event.
         * @return true
         */
        public boolean isRelative() {
            return getTo() != null ;
        }
    }
}
