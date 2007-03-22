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

    private TimePoint when;
    private TimePoint until;
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
    public TimePoint getUntil() {
        return this.until;
    }

    /**
     * Set the value of until.
     * @param until The new value of until
     */
    public void setUntil( TimePoint until ) {
        this.until = until;
    }

    /**
     * Return the value of when.
     */
    public TimePoint getWhen() {
        return this.when;
    }

    /**
     * Set the value of when.
     * @param when The new value of when
     */
    public void setWhen( TimePoint when ) {
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
        private Duration delta;

        /**
         * Default constructor.
         */
        public RelativeTimePoint() {
        }

        /**
         * Return the value of delta.
         */
        public Duration getDelta() {
            return this.delta;
        }

        /**
         * Set the value of delta.
         * @param delta The new value of delta
         */
        public void setDelta( Duration delta ) {
            this.delta = delta;
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
    }
}
