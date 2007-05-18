// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.util.Area;
import com.mindalliance.channels.util.TimePeriod;

/**
 * Periods of time when an agent can be reached, an information
 * resource accessed or a channel used, and when they cannot.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @composed - normal    * Circumstance
 * @composed - exception * Circumstance
 */
public class Availability {

    private List<Situation> normalSituations = new ArrayList<Situation>();
    private List<Situation> exceptions = new ArrayList<Situation>();

    /**
     * Default constructor.
     */
    public Availability() {
        super();
    }

    //-------------------------
    /**
     * Return the value of exceptions.
     */
    public List<Situation> getExceptions() {
        return this.exceptions;
    }

    /**
     * Set the value of exceptions.
     * @param exceptions The new value of exceptions
     */
    public void setExceptions( List<Situation> exceptions ) {
        this.exceptions = exceptions;
    }

    /**
     * Add an exceptional situation.
     * @param exception the situation
     */
    public void addException( Situation exception ) {
        this.exceptions.add( exception );
    }

    /**
     * Remove an exceptional situation.
     * @param exception the situation
     */
    public void removeException( Situation exception ) {
        this.exceptions.remove( exception );
    }

    //-------------------------
    /**
     * Return the value of normalSituations.
     */
    public List<Situation> getNormalSituations() {
        return this.normalSituations;
    }

    /**
     * Set the value of normalSituations.
     * @param normalSituations The new value of normalSituations
     */
    public void setNormalSituations( List<Situation> normalSituations ) {
        this.normalSituations = normalSituations;
    }

    /**
     * Add a normal situation.
     * @param situation the situation
     */
    public void addNormalSituation( Situation situation ) {
        this.normalSituations.add( situation );
    }

    /**
     * Remove a normal situation.
     * @param situation the situation
     */
    public void removeNormalSituation( Situation situation ) {
        this.normalSituations.remove( situation );
    }

    //=========================================
    /**
     * A point in space and time.
     * @opt attributes
     */
    public static class Situation {

        private String description;
        private TimePeriod time;
        private Area location;

        /**
         * Default constructor.
         */
        public Situation() {
            super();
        }

        /**
         * Default constructor.
         * @param time the time
         * @param location the location
         */
        public Situation( TimePeriod time, Area location ) {
            this();
            setTime( time );
            setLocation( location );
        }

        /**
         * Return the value of location.
         */
        public Area getLocation() {
            return this.location;
        }

        /**
         * Set the value of location.
         * @param location The new value of location
         */
        public void setLocation( Area location ) {
            this.location = location;
        }

        /**
         * Return the value of time.
         */
        public TimePeriod getTime() {
            return this.time;
        }

        /**
         * Set the value of time.
         * @param time The new value of time
         */
        public void setTime( TimePeriod time ) {
            this.time = time;
        }

        /**
         * Return the value of description.
         */
        public String getDescription() {
            return this.description;
        }

        /**
         * Set the value of description.
         * @param description The new value of description
         */
        public void setDescription( String description ) {
            this.description = description;
        }
    }
}
