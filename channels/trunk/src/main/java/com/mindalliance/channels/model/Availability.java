// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.util.GUID;

/**
 * Periods of time when an agent can be reached, an information
 * resource accessed or a channel used, and others when they cannot.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Availability extends AbstractModelObject {

    private List<Situation> normal = new ArrayList<Situation>();
    private List<Situation> exceptions = new ArrayList<Situation>();

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Availability( GUID guid ) {
        super( guid );
    }

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
     * Return the value of normal.
     */
    public List<Situation> getNormal() {
        return this.normal;
    }

    /**
     * Set the value of normal.
     * @param normal The new value of normal
     */
    public void setNormal( List<Situation> normal ) {
        this.normal = normal;
    }

    //=================================
    /**
     * A place and time.
     */
    public static class Situation {

        private TimePeriod time;
        private List<Area> locations;

        /**
         * Default constructor.
         */
        public Situation() {
        }

        /**
         * Return the value of locations.
         */
        public List<Area> getLocations() {
            return this.locations;
        }

        /**
         * Set the value of locations.
         * @param locations The new value of locations
         */
        public void setLocations( List<Area> locations ) {
            this.locations = locations;
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
    }
}
