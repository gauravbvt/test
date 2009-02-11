package com.mindalliance.channels;

import java.util.List;

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
    long getScenarioCount();

    /**
     * @param scenario the scenario that will contain this part
     * @return a new default part.
     */
    Part createPart( Scenario scenario );

    /**
     * @param scenario the scenario that will contain this connector
     * @return a new connector.
     */
    Connector createConnector( Scenario scenario );

    /**
     * Create a new internal flow.
     * @param source the source
     * @param target the target
     * @param name the name of the flow
     * @return a new flow.
     */
    InternalFlow createInternalFlow( Node source, Node target, String name );

    /**
     * Create a new external flow.
     * @param source the source
     * @param target the target
     * @param name the name of the flow
     * @return a new flow.
     */
    ExternalFlow createExternalFlow( Node source, Node target, String name );

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
     * Get all objects of the given class.
     * @param clazz the given subclass of model object.
     * @param <T> a subclass of model object.
     * @return a collection
     */
    <T extends ModelObject> List<T> getAll( Class<T> clazz );

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
