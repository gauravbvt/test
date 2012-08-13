/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core;

import java.util.Date;
import java.util.Iterator;

/**
 * Data access object for persistent objects.
 */
public interface PersistentObjectDao {

    /**
     * Delete an object from the database.
     *
     * @param clazz a class of persistent objects
     * @param id a string
     * @param <T> the specific kind of persistent object
     */
    <T extends PersistentObject> void delete( Class<T> clazz, String id );

    /**
     * Retrieve persistent object given its unique id.
     *
     * @param clazz a class of persistent objects
     * @param id a string
     * @param <T> the specific kind of persistent object
     * @return a persistent object or null
     */
    <T extends PersistentObject> T find( Class<T> clazz, String id );

    /**
     * Iterate over everything from a class, sorted by descending date.
     *
     * @param clazz the class
     * @param <T> the specific kind of persistent object
     * @return an iterator
     */
    <T extends PersistentObject> Iterator<T> findAll( Class<T> clazz );

    /**
     * Iterate over objects addressed to a specific user ("toUsername" property) and not "fromUsername".
     *
     *
     * @param clazz the object class
     * @param username the specific user's name
     * @param planner true if the user is a planner
     * @param usersBroadcast special "toUsername" value for broadcasts to all users
     * @param plannersBroadcast special "toUsername" value for broadcasts to all planners
     * @return iterator on relevant objects
     */
    <T extends PersistentObject> Iterator<T> findAllExceptUser(
            Class<T> clazz, String username, boolean planner, String usersBroadcast, String plannersBroadcast );

    /**
     * Iterate over objects with "fromUsername" attribute equals to a given username, sorted by descending date.
     *
     * @param clazz the class of objects
     * @param username the given username
     * @param <T> the specific kind of persistent object
     * @return an iterator
     */
    <T extends PersistentObject> Iterator<T> findAllFrom( Class<T> clazz, String username );

    /**
     * Find latest object from a user after a specific date.
     *
     * @param clazz a class of persistent objects
     * @param username the user name
     * @param startupDate specified date
     * @param <T> the specific kind of persistent object
     * @return the last object
     */
    <T extends PersistentObject> T findLatestFrom( Class<T> clazz, String username, Date startupDate );

    /**
     * Store a persistent object.
     *
     * @param po a persistent object.
     */
    void store( PersistentObject po );

    /**
     * Update a property of a persistent object.
     *
     * @param clazz the class of the object
     * @param id its id
     * @param property the name of the property
     * @param value the value of the property
     * @param <T> the specific kind of persistent object
     */
    <T extends PersistentObject> void update( Class<T> clazz, String id, String property, Object value );
}
