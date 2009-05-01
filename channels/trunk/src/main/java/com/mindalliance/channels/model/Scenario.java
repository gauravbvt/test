package com.mindalliance.channels.model;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.NotFoundException;

/**
 * A scenario in the plan.
 * Provides an iterator on its nodes.
 */
@Entity
public class Scenario extends ModelObject {

    /**
     * The default name for new scenarios.
     */
    public static final String DEFAULT_NAME = "Untitled";

    /**
     * The default description for new scenarios.
     */
    public static final String DEFAULT_DESCRIPTION = "A scenario";

    /**
     * Initial node capacity.
     */
    private static final int INITIAL_CAPACITY = 20;

    /**
     * Nodes, indexed by id.
     */
    private Map<Long, Node> nodeIndex;
    /**
     * Whether not meant to be caused during the plan.
     */
    private boolean incident;
    /**
     * Parts that can initiate this scenario.
     */
    private Set<Part> initiators = new HashSet<Part>();
    /**
     * The spatial scope of the scenario.
     * Everywhere if null.
     */
    private Place location;
    /**
     * Whether the scenario can terminate on its own.
     */
    private boolean selfTerminating;
    /**
     * How long it usually takes for the scenario to terminate on its own, if it does.
     */
    private Delay completionTime = new Delay();

    /**
     * The query service in charge of this scenario.
     */
    private transient QueryService queryService;

    public Scenario() {
        setNodeIndex( new HashMap<Long, Node>( INITIAL_CAPACITY ) );
    }

    @OneToMany( cascade = {CascadeType.REMOVE}, mappedBy = "scenario" )
    @MapKey( name = "id" )
    Map<Long, Node> getNodeIndex() {
        return nodeIndex;
    }

    void setNodeIndex( Map<Long, Node> nodeIndex ) {
        this.nodeIndex = nodeIndex;
    }

    public boolean isIncident() {
        return incident;
    }

    public void setIncident( boolean incident ) {
        this.incident = incident;
    }

    @OneToMany
    public Set<Part> getInitiators() {
        return initiators;
    }

    public void setInitiators( Set<Part> initiators ) {
        this.initiators = initiators;
    }

    public Place getLocation() {
        return location;
    }

    public void setLocation( Place location ) {
        this.location = location;
    }

    public boolean isSelfTerminating() {
        return selfTerminating;
    }

    public void setSelfTerminating( boolean selfTerminating ) {
        this.selfTerminating = selfTerminating;
    }

    public Delay getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime( Delay completionTime ) {
        this.completionTime = completionTime;
    }

    /**
     * Iterate over the nodes in this scenario.
     * There should always be at least a node in the scenario.
     * The nodes are sorted on their tasks.
     *
     * @return an iterator on sorted nodes
     */
    public Iterator<Node> nodes() {
        List<Node> nodes = new ArrayList<Node>();
        nodes.addAll( getNodeIndex().values() );
        Collections.sort( nodes, new Comparator<Node>() {
            /**
             * {@inheritDoc}
             */
            public int compare( Node o1, Node o2 ) {
                Collator collator = Collator.getInstance();
                return collator.compare(
                        o1.isConnector() ? "\uFF5A\uFF5A" : ( (Part) o1 ).getTask(),
                        o2.isConnector() ? "\uFF5A\uFF5A" : ( (Part) o2 ).getTask() );
            }
        } );
        return nodes.iterator();
    }

    /**
     * @return the number of nodes in this scenario
     */
    @Transient
    public int getNodeCount() {
        return getNodeIndex().size();
    }

    /**
     * Convenience accessor for tests.
     *
     * @param queryService   the underlying store
     * @param actor the actor for the new part
     * @param task  the task of the new part
     * @return the new part
     */
    public Part createPart( QueryService queryService, Actor actor, String task ) {
        Part result = queryService.createPart( this );
        result.setActor( actor );
        result.setTask( task );
        addNode( result );
        return result;
    }

    /**
     * Convenience accessor for tests.
     *
     * @param queryService  the underlying store
     * @param role the role for the new part
     * @param task the task of the new part
     * @return the new part
     */
    public Part createPart( QueryService queryService, Role role, String task ) {
        Part result = queryService.createPart( this );
        result.setRole( role );
        result.setTask( task );
        addNode( result );
        return result;
    }

    /**
     * Add a new node to this scenario.
     *
     * @param node the new node
     */
    public void addNode( Node node ) {
        nodeIndex.put( node.getId(), node );
        node.setScenario( this );
    }

    /**
     * Remove a node from this scenario.
     * Quietly succeeds if node is not part of the scenario
     *
     * @param node the node to remove.
     */
    public void removeNode( Node node ) {
        if ( getNodeIndex().containsKey( node.getId() )
                && ( node.isConnector() || hasMoreThanOnePart() ) ) {
            Iterator<Flow> ins = node.requirements();
            while ( ins.hasNext() ) {
                ins.next().disconnect();
            }
            Iterator<Flow> outs = node.outcomes();
            while ( outs.hasNext() ) {
                outs.next().disconnect();
            }

            if ( node.isConnector() ) {
                List<ExternalFlow> toDisconnect = new ArrayList<ExternalFlow>();
                Iterator<ExternalFlow> xf = ( (Connector) node ).externalFlows();
                while ( xf.hasNext() ) {
                    toDisconnect.add(xf.next());
                }
                // Avoid ConcurrentModificationException
                for (ExternalFlow flow : toDisconnect) {
                    flow.disconnect();
                }
            }

            queryService.remove( node );
            nodeIndex.remove( node.getId() );
            node.setScenario( null );
        }
    }

    private boolean hasMoreThanOnePart() {
        Iterator<Part> parts = parts();
        parts.next();
        // Note: scenario must always have at least one part
        return parts.hasNext();
    }

    /**
     * Get a node, given its id.
     *
     * @param id the id
     * @return the node, or null if not found
     */
    public Node getNode( long id ) {
        return nodeIndex.get( id );
    }

    /**
     * Remove any connections to the outside world
     * (essentially, anything connected to an input or output connector).
     */
    public void disconnect() {
        for ( Node n : nodeIndex.values() ) {
            if ( n.isConnector() )
                ( (Connector) n ).disconnect();
        }

    }

    /**
     * Iterates over inputs of this scenario.
     *
     * @return an iterator on connectors having outcomes
     */
    @SuppressWarnings( {"unchecked"} )
    public Iterator<Connector> inputs() {
        return (Iterator<Connector>) new FilterIterator( nodes(), new Predicate() {
            public boolean evaluate( Object object ) {
                Node n = (Node) object;
                return n.isConnector() && n.outcomes().hasNext();
            }
        } );
    }

    /**
     * Iterates over the parts of this scenario.
     *
     * @return an iterator on parts
     */
    @SuppressWarnings( {"unchecked"} )
    public Iterator<Part> parts() {
        return (Iterator<Part>) new FilterIterator( nodes(), new Predicate() {
            public boolean evaluate( Object object ) {
                Node n = (Node) object;
                return n.isPart();
            }
        } );
    }

    /**
     * Iterates over outputs of this scenario.
     *
     * @return an iterator on connectors having requirements
     */
    @SuppressWarnings( {"unchecked"} )
    public Iterator<Connector> outputs() {
        return (Iterator<Connector>) new FilterIterator( nodes(), new Predicate() {
            public boolean evaluate( Object object ) {
                Node n = (Node) object;
                return n.isConnector() && n.requirements().hasNext();
            }
        } );
    }

    /**
     * Get an iterator on the flows of the scenario, sorted alphabetically.
     *
     * @return an iterator on unique nodes
     */
    public Iterator<Flow> flows() {
        return new FlowIterator();
    }

    /**
     * Find node to display when none was specified.
     *
     * @return the first part in this scenario
     */
    @Transient
    public Part getDefaultPart() {
        return parts().next();
    }

    /**
     * Find parts played by a given role.
     *
     * @param role the role, possibly Role.UNKNOWN
     * @return the appropriate parts
     */
    public List<Part> findParts( Role role ) {
        List<Part> result = new ArrayList<Part>();

        for ( Iterator<Part> parts = parts(); parts.hasNext(); ) {
            Part part = parts.next();
            if ( part.isPlayedBy( role ) )
                result.add( part );
        }

        return result;
    }

    /**
     * Find parts played by a given organization, when no roles or actors have been specified.
     *
     * @param organization the organization, possibly Organization.UNKNOWN
     * @return the appropriate parts
     */
    public List<Part> findParts( Organization organization ) {
        List<Part> partsForRole = new ArrayList<Part>();
        Iterator<Part> parts = parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.isIn( organization ) && part.getActor() == null && part.getRole() == null )
                partsForRole.add( part );
        }
        return partsForRole;
    }

    /**
     * Find parts played by a given role in a given organization.
     *
     * @param organization the organization, possibly Organization.UNKNOWN
     * @param role         the role, possibly Role.UNKNOWN
     * @return the appropriate parts
     */
    public List<Part> findParts( Organization organization, Role role ) {
        List<Part> partsForRole = new ArrayList<Part>();
        Iterator<Part> parts = parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.isIn( organization ) && part.isPlayedBy( role ) )
                partsForRole.add( part );
        }
        return partsForRole;
    }

    /**
     * Find roles in a given organization used in this scenario.
     *
     * @param organization the organization, possibly Organization.UNKNOWN
     * @return the appropriate roles.
     */
    public List<Role> findRoles( Organization organization ) {
        boolean hasUnknown = false;

        Set<Role> roles = new HashSet<Role>();
        Iterator<Part> parts = parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.isIn( organization ) ) {
                if ( part.getRole() != null )
                    roles.add( part.getRole() );
                else
                    hasUnknown = true;
            }
        }

        List<Role> list = new ArrayList<Role>( roles );
        Collections.sort( list, new Comparator<Role>() {
            /** {@inheritDoc} */
            public int compare( Role o1, Role o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );
        if ( hasUnknown )
            list.add( Role.UNKNOWN );
        return list;
    }

    /**
     * FInd a flow given its id.
     *
     * @param id a long
     * @return a flow
     * @throws com.mindalliance.channels.NotFoundException if not found
     */
    public Flow findFlow( long id ) throws NotFoundException {
        Flow flow = null;
        Iterator<Flow> flows = flows();
        while ( flow == null && flows.hasNext() ) {
            Flow f = flows.next();
            if ( f.getId() == id ) flow = f;
        }
        if ( flow == null ) throw new NotFoundException();
        else return flow;
    }

    @Transient
    public QueryService getQueryService() {
        return queryService;
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

    /**
     * Count the parts.
     *
     * @return an int
     */
    public int countParts() {
        int count = 0;
        for ( Node node : nodeIndex.values() ) {
            if ( node.isPart() ) count++;
        }
        return count;
    }

    /**
     * Add part as initiator.
     *
     * @param part a part
     */
    public void addInitiator( Part part ) {
        initiators.add( part );
        if ( part.getInitiatedScenario() != this ) {
            part.setInitiatedScenario( this );
        }
    }

    /**
     * Remove part as initiator.
     *
     * @param part a part
     */
    public void removeInitiator( Part part ) {
        initiators.remove( part );
        if ( part.getInitiatedScenario() == this ) {
            part.setInitiatedScenario( null );
        }
    }

    public void beforeRemove( QueryService dataQueryObject ) {
        for ( Part part : initiators ) {
            part.setInitiatedScenario( null );
        }
    }

    /**
     * Whether this scenario has initiators.
     * @return
     */
    @Transient
    public boolean isInitiated() {
        return !getInitiators().isEmpty();
    }

    //=================================================
    /**
     * An iterator that walks through all flow in the scenario.
     * This is done by iterating on all outcome flows of all nodes,
     * in node-order.
     */
    private final class FlowIterator implements Iterator<Flow> {

        /**
         * Iterator on nodes.
         */
        private final Iterator<Node> nodeIterator;

        /**
         * Iterator on the outcomes of the current node.
         */
        private Iterator<Flow> outcomeIterator;

        /**
         * Iterator on the external requirements of the current node.
         */
        private Iterator<Flow> reqIterator;

        private FlowIterator() {
            nodeIterator = nodes();
            if ( nodeIterator.hasNext() )
                setIterators( nodeIterator.next() );
            else {
                outcomeIterator = Collections.<Flow>emptyList().iterator();
                reqIterator = Collections.<Flow>emptyList().iterator();
            }
        }

        @SuppressWarnings( { "unchecked" } )
        private void setIterators( Node node ) {
            outcomeIterator = node.outcomes();
            reqIterator = (Iterator<Flow>) new FilterIterator(
                    node.requirements(),
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return !( (Flow) object ).isInternal();
                        }
                    } );
        }

        public boolean hasNext() {
            while ( !outcomeIterator.hasNext() && !reqIterator.hasNext()
                    && nodeIterator.hasNext() )
                setIterators( nodeIterator.next() );

            return outcomeIterator.hasNext() || reqIterator.hasNext();
        }

        public Flow next() {
            if ( !hasNext() )
                throw new NoSuchElementException();
            return outcomeIterator.hasNext() ?
                    outcomeIterator.next() : reqIterator.next();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
