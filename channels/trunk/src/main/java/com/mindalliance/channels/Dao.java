package com.mindalliance.channels;

import java.util.Iterator;

/**
 * Protocol for manipulating objects.
 * Implementations should ensure that there is at least one scenario available.
 */
public interface Dao {

    /**
     * The initial number of scenario slots.
     * This should be close to the observed average scenarios.
     */
    int INITIAL_CAPACITY = 10;

    /**
     * @return the total number of scenarios.
     */
    int getScenarioCount();

    /**
     * @return a new default part.
     */
    Part createPart();

    /**
     * @return a new connector.
     */
    Connector createConnector();

    /**
     * Find a model object given its id.
     * @param clazz the subclass of modelobject
     * @param id the id
     * @param <T> a subclass of modelobject
     * @return the object
     * @throws NotFoundException when not found
     */
     <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException;

    /**
     * Iterate on objects of the given class.
     * @param clazz the given subclass of model object.
     * @param <T> a subclass of model object.
     * @return an iterator
     */
    <T extends ModelObject> Iterator<T> iterate( Class<T> clazz );

    /**
     * Add a model object.
     * @param object the model object.
     */
    void add( ModelObject object );

    /**
     * Remove a persistent model object.
     * Last scenario will not be deleted.
     * @param object the object
     */
    void remove( ModelObject object );

    /**
     * Whether the resource spec exists independently of parts
     * @param resourceSpec the resource spec
     * @return a boolean
     */
    boolean isPermanent( ResourceSpec resourceSpec );


}
