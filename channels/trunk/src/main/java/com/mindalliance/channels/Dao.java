package com.mindalliance.channels;

import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;

import java.util.List;

/**
 * Protocol for manipulating objects.
 * Implementations should ensure that there is at least one scenario available.
 */
public interface Dao extends Service {

    /**
     * The initial number of scenario slots.
     * This should be close to the observed average scenarios.
     */
    int INITIAL_CAPACITY = 10;

    /**
     * Make a plan with unique id.
     *
     * @return
     */
    Plan createPlan();

    /**
     * @return the total number of scenarios.
     */
    long getScenarioCount();

    /**
     * Create a part with given id if not null.
     *
     * @param scenario the scenario that will contain this part
     * @param id a Long
     * @return a new default part.
     */
    Part createPart( Scenario scenario, Long id );

    /**
     * @param scenario the scenario that will contain this connector
     * @param id a Long
     * @return a new connector.
     */
    Connector createConnector( Scenario scenario, Long id );

    /**
     * Create a new internal flow, giving it provided id if not null.
     *
     * @param source the source
     * @param target the target
     * @param name   the name of the flow
     * @param id     Long or null
     * @return a new flow.
     */
    InternalFlow createInternalFlow( Node source, Node target, String name, Long id );

    /**
     * Create a new external flow, giving it provided id if not null.
     *
     * @param source the source
     * @param target the target
     * @param name   the name of the flow
     * @param id     Long or null
     * @return a new flow.
     */
    ExternalFlow createExternalFlow( Node source, Node target, String name, Long id );

    /**
     * Find a model object given its id.
     *
     * @param clazz the subclass of modelobject
     * @param id    the id
     * @param <T>   a subclass of modelobject
     * @return the object
     * @throws NotFoundException when not found
     */
    <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException;

    /**
     * Get all objects of the given class.
     *
     * @param clazz the given subclass of model object.
     * @param <T>   a subclass of model object.
     * @return a collection
     */
    <T extends ModelObject> List<T> list( Class<T> clazz );

    /**
     * Add a model object with a new id.
     *
     * @param object the model object.
     */
    void add( ModelObject object );

    /**
     * Add a model object with a given id.
     *
     * @param object the model object.
     * @param id     a long
     */
    void add( ModelObject object, Long id );

    /**
     * Update a model object.
     *
     * @param object the model object.
     */
    void update( ModelObject object );

    /**
     * Remove a persistent model object.
     * Last scenario will not be deleted.
     *
     * @param object the object
     */
    void remove( ModelObject object );

    /**
     * Find the first model object of a given class and name.
     *
     * @param clazz the subclass of model object
     * @param name  the name looked for
     * @param <T>   a subclass of model object
     * @return the object or null if not found
     */
    <T extends ModelObject> T find( Class<T> clazz, String name );

    /**
     * Commit changes to persistent store.
     */
    void flush();

    /**
     * Get last assigned id.
     *
     * @return a long
     */
    Long getLastAssignedId();

    /**
     * Set last assigned id.
     *
     * @param lastId a long
     */
    void setLastAssignedId( Long lastId );

    /**
     * Handle post command execution event.
     *
     * @param command a command
     */
    void onAfterCommand( Command command );

    /**
     * Load persisted data, if any.
     */
    void load();

    /**
     * Called after right after initialization.
     */
    void afterInitialize();

    /**
     * Called when application is terminated.
     */
    void onDestroy();

}
