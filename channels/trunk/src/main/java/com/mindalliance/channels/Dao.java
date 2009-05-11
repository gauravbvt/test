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
     * @return
     */
    Plan createPlan();

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
     *
     * @param source the source
     * @param target the target
     * @param name   the name of the flow
     * @return a new flow.
     */
    InternalFlow createInternalFlow( Node source, Node target, String name );

    /**
     * Create a new external flow.
     *
     * @param source the source
     * @param target the target
     * @param name   the name of the flow
     * @return a new flow.
     */
    ExternalFlow createExternalFlow( Node source, Node target, String name );

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
     * Add a model object.
     *
     * @param object the model object.
     */
    void add( ModelObject object );

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
