package com.mindalliance.channels.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A node in the flow graph
 */
public abstract class Node extends NamedObject {

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

    protected Node() {
        setOutcomes( new TreeSet<Flow>() );
        setRequirements( new TreeSet<Flow>() );
    }

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

    private Set<Flow> getOutcomes() {
        return outcomes;
    }

    /**
     * Set outcomes, rebuilding the index.
     * Package-visible for tests.
     * @param outcomes the new outcomes
     */
    final void setOutcomes( Set<Flow> outcomes ) {
        this.outcomes = new TreeSet<Flow>( outcomes );
        outIndex = new HashMap<Long,Flow>( INITIAL_CAPACITY );
        for ( Flow f: outcomes )
            outIndex.put( f.getId(), f );
    }

    /**
     * Iterates over outcomes, alphabetically by print string.
     * @return a flow iterator
     */
    public Iterator<Flow> outcomes() {
        return getOutcomes().iterator();
    }

    /**
     * Add an outcome to this node.
     * @param outcome the outcome
     */
    public void addOutcome( Flow outcome ) {
        getOutcomes().add( outcome );
        outIndex.put( outcome.getId(), outcome );
    }

    /**
     * Remove an outcome from this node.
     * @param outcome the outcome
     */
    public void removeOutcome( Flow outcome ) {
        getOutcomes().remove( outcome );
        outIndex.remove( outcome.getId() );
    }

    private Set<Flow> getRequirements() {
        return requirements;
    }

    /**
     * Set requirements, rebuilding the index.
     * Package-visible for tests.
     * @param requirements the new requirements
     */
    final void setRequirements( Set<Flow> requirements ) {
        this.requirements = new TreeSet<Flow>( requirements );
        reqIndex = new HashMap<Long,Flow>( INITIAL_CAPACITY );
        for ( Flow f: requirements )
            reqIndex.put( f.getId(), f );
    }

    /**
     * Iterates over requirements, alphabetically by print string.
     * @return a flow iterator
     */
    public Iterator<Flow> requirements() {
        return getRequirements().iterator();
    }

    /**
     * Add a requirement to this node.
     * @param requirement the requirement
     */
    public void addRequirement( Flow requirement ) {
        getRequirements().add( requirement );
        reqIndex.put( requirement.getId(), requirement );
    }

    /**
     * Remove a requirement from this node.
     * @param requirement the requirement
     */
    public void removeRequirement( Flow requirement ) {
        getRequirements().remove( requirement );
        reqIndex.remove( requirement.getId() );
    }

    public boolean isPart() {
        return false;
    }

    public boolean isScenarioNode() {
        return false;
    }

    public boolean isConnector() {
        return false;
    }

}
