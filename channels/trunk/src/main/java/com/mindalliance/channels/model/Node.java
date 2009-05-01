package com.mindalliance.channels.model;

import com.mindalliance.channels.util.SemMatch;
import com.mindalliance.channels.QueryService;
import org.apache.commons.collections.iterators.IteratorChain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A node in the flow graph
 */
@Entity
public abstract class Node extends ModelObject implements ScenarioObject {

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
        setOutcomes( new HashMap<Long, Flow>() );
        setRequirements( new HashMap<Long, Flow>() );
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

    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    @JoinTable( name = "Node_Outs" ) @MapKey( name = "id" )
    protected Map<Long,Flow> getOutcomes() {
        return outcomes;
    }

    /**
     * Set outcomes, rebuilding the index.
     * Package-visible for tests.
     * @param outcomes the new outcomes
     */
    void setOutcomes( Map<Long, Flow> outcomes ) {
        this.outcomes = outcomes;
    }

    /**
     * Iterates over outcomes, alphabetically by print string.
     * @return a flow iterator
     */
    public Iterator<Flow> outcomes() {
        List<Flow> flows = new ArrayList<Flow>();
        flows.addAll( getOutcomes().values() );
        Collections.sort( flows, new Comparator<Flow>() {
            /**
             * {@inheritDoc}
             */
            public int compare( Flow flow, Flow other ) {
                // Sort on significance to source
                if ( flow.getSignificanceToSource().ordinal() == other.getSignificanceToSource().ordinal() ) {
                    // if same, sort on whether required (triggers, iscritical to or terminates target)
                    if ( flow.isRequired() == other.isRequired() ) {
                        // if same, sort on max delay
                        int comp = flow.getMaxDelay().compareTo( other.getMaxDelay() );
                        if ( comp == 0 ) {
                            // if same, sort on name
                            return Collator.getInstance().compare( flow.getName(), other.getName() );
                        } else {
                            return comp;
                        }
                    } else {
                        // if both are required, sort on information
                        return flow.isRequired() ? -1 : 1;
                    }
                } else {
                    return flow.getSignificanceToSource().ordinal() < other.getSignificanceToSource().ordinal()
                            ? -1
                            : 1;
                }
            }
        } );
        return flows.iterator();
    }

    /**
     * Create a new outcome for this node.
     * @param queryService the underlying store
     * @return an internal flow to a new connector
     */
    public Flow createOutcome( QueryService queryService ) {
        return queryService.connect(
                this,
                queryService.createConnector( getScenario() ),
                DEFAULT_FLOW_NAME );
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

    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    @JoinTable( name = "Node_Reqs" ) @MapKey( name = "id" )
    protected Map<Long,Flow> getRequirements() {
        return requirements;
    }

    /**
     * Set requirements, rebuilding the index.
     * Package-visible for tests.
     * @param requirements the new requirements
     */
    void setRequirements( Map<Long, Flow> requirements ) {
        this.requirements = requirements;
    }

    /**
     * Iterates over requirements, alphabetically by print string.
     * @return a flow iterator
     */
    public Iterator<Flow> requirements() {
        List<Flow> flows = new ArrayList<Flow>();
        flows.addAll( getRequirements().values() );
        Collections.sort( flows, new Comparator<Flow>() {
            /**
             * {@inheritDoc}
             */
            public int compare( Flow flow, Flow other ) {
                // Sort on significance to target
                if ( flow.getSignificanceToTarget().ordinal() == other.getSignificanceToTarget().ordinal() ) {
                    // If same significance, sort on information
                    return Collator.getInstance().compare( flow.getName(), other.getName() );
                } else {
                    return flow.getSignificanceToTarget().ordinal() < other.getSignificanceToTarget().ordinal()
                            ? -1
                            : 1;
                }
            }
        } );
        return flows.iterator();
    }

    /**
     * Create and add a new requirement.
     * @param queryService the underyling store
     * @return a flow from a new connector to this node
     */
    public Flow createRequirement( QueryService queryService ) {
        return queryService.connect( queryService.createConnector( getScenario() ), this, DEFAULT_FLOW_NAME );
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

    /**
     * Get all flows attached to this part, requirements and outcomes.
     * @return a flow iterator
     */
    @SuppressWarnings( { "unchecked" } )
    public Iterator<Flow> flows() {
        return (Iterator<Flow>) new IteratorChain( requirements(), outcomes() );
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

    /**
     * Test if this node is connected to another node by a flow of a given name.
     *
     * @param outcome test if node is an outcome, otherwise a requirement
     * @param node    the other node
     * @param name    the name of the flow
     * @return true if connected.
     */
    public boolean isConnectedTo( boolean outcome, Node node, String name ) {
        boolean result = false;
        Map<Long, Flow> flows = outcome ? outcomes : requirements;
        for ( Iterator<Flow> it = flows.values().iterator(); !result && it.hasNext(); ) {
            Flow f = it.next();
            result = name.equals( f.getName() ) && f.isConnectedTo( outcome, node );
        }

        return result;
    }

    /**
     * Find all outcome flows that are required.
     *
     * @return a list of flows
     */
    public List<Flow> requiredOutcomes() {
        List<Flow> requiredFlows = new ArrayList<Flow>();
        for ( Flow out : outcomes.values() ) {
            if ( out.isRequired() ) {
                requiredFlows.add( out );
            }
        }
        return requiredFlows;
    }

    /**
     * Whether this node has more than one outcome of a given name
     * @param name the name of a flow
     * @return a boolean
     */
    public boolean hasMultipleOutcomes( String name ) {
        int count = 0;
        for ( Flow outcome : outcomes.values() ) {
            if ( SemMatch.same( outcome.getName(), name ) ) count++;
        }
        return count > 1;
    }

    /**
     * Whether this node has more than one requirement of a given name
     * @param name the name of a flow
     * @return a boolean
     */
    public boolean hasMultipleRequirements( String name ) {
        int count = 0;
        for ( Flow req : requirements.values() ) {
            if ( SemMatch.same( req.getName(), name ) ) count++;
        }
        return count > 1;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean isUndefined() {
        return super.isUndefined() && requirements.isEmpty() && outcomes.isEmpty();
    }


}
