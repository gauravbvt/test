package com.mindalliance.channels;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * A node in the flow graph
 */
@Entity
public abstract class Node extends ModelObject {

    /** Initial capacity of outcome and requirement flows. */
    private static final int INITIAL_CAPACITY = 5;

    /** The name for new flows. */
    private static final String DEFAULT_FLOW_NAME = "";

    /** All requirements, indexed by id. */
    private Map<Long,Flow> requirements;

    /** All outcomes, indexed by id. */
    private Map<Long,Flow> outcomes;

    /** The unique scenario containing this node. */
    private Scenario scenario;

    protected Node() {
        setOutcomes( new HashMap<Long,Flow>() );
        setRequirements( new HashMap<Long,Flow>() );
    }

    /**
     * Get a long string that can be used as a title for this node.
     * @return a generated short description
     */
    @Transient
    public abstract String getTitle();

    /**
     * Find a flow connected to this node, given its id.
     * @param id the id
     * @return a flow or null
     */
    public Flow getFlow( long id ) {
        Flow result = requirements.get( id );
        if ( result == null )
            result = outcomes.get( id );

        return result;
    }

    @OneToMany( cascade = CascadeType.ALL )
    @JoinTable( name = "Node_Outs" ) @MapKey( name = "id" )
    private Map<Long,Flow> getOutcomes() {
        return outcomes;
    }

    /**
     * Set outcomes, rebuilding the index.
     * Package-visible for tests.
     * @param outcomes the new outcomes
     */
    void setOutcomes( Map<Long,Flow> outcomes ) {
        if ( this.outcomes != null )
            for ( Flow f: new HashSet<Flow>( this.outcomes.values() ) )
                removeOutcome( f );
        this.outcomes = outcomes;
        for ( Flow f: outcomes.values() )
            f.setSource( this );
    }

    /**
     * Iterates over outcomes, alphabetically by print string.
     * @return a flow iterator
     */
    public Iterator<Flow> outcomes() {
        return getOutcomes().values().iterator();
    }

    /**
     * Create a new outcome for this node.
     * @param service the underlying store
     * @return an internal flow to a new connector
     */
    public Flow createOutcome( Service service ) {
        return service.connect( this, service.createConnector( getScenario() ), DEFAULT_FLOW_NAME );
    }

    /**
     * Add an outcome to this node.
     * @param outcome the outcome
     */
    public void addOutcome( Flow outcome ) {
        outcomes.put( outcome.getId(), outcome );
        outcome.setSource( this );
    }

    /**
     * Remove an outcome from this node.
     * Removes the source node if a connector.
     * @param outcome the outcome
     */
    void removeOutcome( Flow outcome ) {
        outcomes.remove( outcome.getId() );
        outcome.setSource( null );
    }

    @OneToMany( cascade = CascadeType.ALL )
    @JoinTable( name = "Node_Reqs" ) @MapKey( name = "id" )
    private Map<Long,Flow> getRequirements() {
        return requirements;
    }

    /**
     * Set requirements, rebuilding the index.
     * Package-visible for tests.
     * @param requirements the new requirements
     */
    void setRequirements( Map<Long,Flow> requirements ) {
        if ( this.requirements != null )
            for ( Flow f: new HashSet<Flow>( this.requirements.values() ) )
                removeRequirement( f );
        this.requirements = requirements;
        for ( Flow f: requirements.values() )
            f.setTarget( this );
    }

    /**
     * Iterates over requirements, alphabetically by print string.
     * @return a flow iterator
     */
    public Iterator<Flow> requirements() {
        return getRequirements().values().iterator();
    }

    /**
     * Create and add a new requirement.
     * @param service the underyling store
     * @return a flow from a new connector to this node
     */
    public Flow createRequirement( Service service ) {
        return service.connect( service.createConnector( getScenario() ), this, DEFAULT_FLOW_NAME );
    }

    /**
     * Add a requirement to this node.
     * @param requirement the requirement
     */
    public void addRequirement( Flow requirement ) {
        requirements.put( requirement.getId(), requirement );
        requirement.setTarget( this );
    }

    /**
     * Remove a requirement from this node.
     * Removes the target node if a connector.
     * @param requirement the requirement
     */
    void removeRequirement( Flow requirement ) {
        requirements.remove( requirement.getId() );
        requirement.setTarget( null );
    }

    @Transient
    public boolean isPart() {
        return false;
    }

    @Transient
    public boolean isConnector() {
        return false;
    }

    @ManyToOne( optional = false )
    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario( Scenario scenario ) {
        this.scenario = scenario;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getTitle();
    }

    /**
      * Test if this node is connected to another node by a flow of a given name.
      * @param outcome test if node is an outcome, otherwise a requirement
      * @param node the other node
      * @param name the name of the flow
      * @return true if connected.
      */
    public boolean isConnectedTo( boolean outcome, Node node, String name ) {
        boolean result = false;
        Map<Long,Flow> flows = outcome ? outcomes : requirements;
        for ( Iterator<Flow> it = flows.values().iterator(); !result && it.hasNext(); ) {
            Flow f = it.next();
            result = name.equals( f.getName() ) && f.isConnectedTo( outcome, node );
        }

        return result;
    }
}
