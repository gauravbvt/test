// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.dao;

import com.mindalliance.mindpeer.model.ModelObject;

import java.io.Serializable;
import java.util.List;

/**
 * Standard operations of model object DAOs.
 * @param <T> the managed subclass of model object
 */
public interface Dao<T extends ModelObject> {

    /**
     * Delete an object.
     * @param object the object to delete
     */
    void delete( T object );

    /**
     * Load an object.
     * @param id the primary key
     * @return the object, or null if not found
     */
    T load( Serializable id );

    /**
     * Save an object.
     * @param object the object to save
     * @return the saved object
     */
    T save( T object );

    /**
     * Find all objects in the database.
     * @return a list of object
     */
    List<T> findAll();

    /**
     * Count the number of objects in the database.
     * @return the number of objects in the database
     */
    int countAll();
}

