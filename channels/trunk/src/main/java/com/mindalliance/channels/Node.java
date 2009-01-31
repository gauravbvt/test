package com.mindalliance.channels;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A node in the flow graph
 */
@Entity @Inheritance( strategy = InheritanceType.SINGLE_TABLE )
public abstract class Node extends ModelObject {

    /** Initial capacity of outcome and requirement flows. */
    private static final int INITIAL_CAPACITY = 5;

    /** The incoming arrows. */
    private Set<Flow> requirements;

    /** The outgoing arrows. */
    private Set<Flow> outcomes;

    /** All requirements, indexed by id. */
    private Map<Long,Flow> reqIndex;

    /** All outcomes, indexed by id. */
    private Map<Long,Flow> outIndex;

    /** The unique scenario containing this node. */
    private Scenario scenario;

    protected Node() {
        setOutcomes( new HashSet<Flow>() );
        setRequirements( new HashSet<Flow>() );
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
        Flow result = reqIndex.get( id );
        if ( result == null )
            result = outIndex.get( id );

        return result;
    }

    @OneToMany
    private Set<Flow> getOutcomes() {
        return outcomes;
    }

    /**
     * Set outcomes, rebuilding the index.
     * Package-visible for tests.
     * @param outcomes the new outcomes
     */
    void setOutcomes( Set<Flow> outcomes ) {
        if ( this.outcomes != null )
            for ( Flow f: new HashSet<Flow>( this.outcomes ) )
                removeOutcome( f );
        this.outcomes = new HashSet<Flow>( outcomes.size() );
        outIndex = new HashMap<Long,Flow>( INITIAL_CAPACITY );
        for ( Flow f: outcomes )
            addOutcome( f );
    }

    /**
     * Iterates over outcomes, alphabetically by print string.
     * @return a flow iterator
     */
    public Iterator<Flow> outcomes() {
        return getOutcomes().iterator();
    }

    /**
     * Create a new outcome for this node.
     * @param service the underlying store
     * @return an internal flow to a new connector
     */
    public Flow createOutcome( Service service ) {
        final Flow flow = getScenario().connect( this, service.createConnector( getScenario() ) );
        addOutcome( flow );
        return flow;
    }

    /**
     * Add an outcome to this node.
     * @param outcome the outcome
     */
    void addOutcome( Flow outcome ) {
        getOutcomes().add( outcome );
        outIndex.put( outcome.getId(), outcome );
        outcome.setSource( this );
    }

    /**
     * Remove an outcome from this node.
     * Removes the source node if a connector.
     * @param outcome the outcome
     */
    void removeOutcome( Flow outcome ) {
        getOutcomes().remove( outcome );
        outIndex.remove( outcome.getId() );
        outcome.setSource( null );
    }

    /**
     * Disconnect all outcomes from this node.
     */
    public void removeAllOutcomes() {
        for ( Flow flow : new HashSet<Flow>( getOutcomes() ) )
            flow.disconnect();
    }

    @OneToMany
    private Set<Flow> getRequirements() {
        return requirements;
    }

    /**
     * Set requirements, rebuilding the index.
     * Package-visible for tests.
     * @param requirements the new requirements
     */
    void setRequirements( Set<Flow> requirements ) {
        if ( this.requirements != null )
            for ( Flow f: new HashSet<Flow>( this.requirements ) )
                removeRequirement( f );
        this.requirements = new HashSet<Flow>( requirements.size() );
        reqIndex = new HashMap<Long,Flow>( INITIAL_CAPACITY );
        for ( Flow f: requirements )
            addRequirement( f );
    }

    /**
     * Iterates over requirements, alphabetically by print string.
     * @return a flow iterator
     */
    public Iterator<Flow> requirements() {
        return getRequirements().iterator();
    }

    /**
     * Create and add a new requirement.
     * @param service the underyling store
     * @return a flow from a new connector to this node
     */
    public Flow createRequirement( Service service ) {
        final Flow flow = getScenario().connect( service.createConnector( getScenario() ), this );
        addRequirement( flow );
        return flow;
    }

    /**
     * Add a requirement to this node.
     * @param requirement the requirement
     */
    void addRequirement( Flow requirement ) {
        getRequirements().add( requirement );
        reqIndex.put( requirement.getId(), requirement );
        requirement.setTarget( this );
    }

    /**
     * Remove a requirement from this node.
     * Removes the target node if a connector.
     * @param requirement the requirement
     */
    void removeRequirement( Flow requirement ) {
        getRequirements().remove( requirement );
        reqIndex.remove( requirement.getId() );
        requirement.setTarget( null );
    }

    /**
     * Disconnect all requirements from this node.
     */
    public void removeAllRequirements() {
        for ( Flow flow : new HashSet<Flow>( getRequirements() ) )
            flow.disconnect();
    }

    @Transient
    public boolean isPart() {
        return false;
    }

    @Transient
    public boolean isConnector() {
        return false;
    }

    @ManyToOne
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

    /** {@inheritDoc} */
    @Override @Transient
    public String getLabel() {
        return getTitle();
    }

    /**
     * Test if this node is connected to another node.
     * @param outcome test if node is an outcome, otherwise a requirement
     * @param node the other node
     * @return true if connected.
     */
    public boolean isConnectedTo( boolean outcome, Node node ) {
        boolean result = false;
        final Set<Flow> flows = outcome ? outcomes : requirements;
        for ( Iterator<Flow> it = flows.iterator(); !result && it.hasNext(); ) {
            final Flow f = it.next();
            if ( outcome && f.getTarget().equals( node ) )
                result = true;
            else if ( !outcome && f.getSource().equals( node ) )
                result = true;
            else if ( !f.isInternal() && node.equals( ( (ExternalFlow) f ).getConnector() ) )
                result = true;
        }

        return result;
    }
}
