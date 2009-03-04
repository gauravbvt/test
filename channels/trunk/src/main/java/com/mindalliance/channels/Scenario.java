package com.mindalliance.channels;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.FetchType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Comparator;
import java.text.Collator;

import com.mindalliance.channels.pages.Project;

/**
 * A scenario in the project.
 * Provides an iterator on its nodes.
 */
@Entity
public class Scenario extends ModelObject {

    // TODO - Add location (as in area) of the scenario (where the scenario applies
    //  - e.g. avian flu case in New Jersey)

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
    private Map<Long, Node> nodeIndex = new HashMap<Long, Node>( INITIAL_CAPACITY );

    public Scenario() {
    }

    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    @Cascade( org.hibernate.annotations.CascadeType.DELETE_ORPHAN )
    Map<Long, Node> getNodeIndex() {
        return nodeIndex;
    }

    void setNodeIndex( Map<Long, Node> nodeIndex ) {
        this.nodeIndex = nodeIndex;
    }

    /**
     * Iterate over the nodes in this scenario.
     * There should always be at least a node in the scenario.
     * The nodes are sorted as follows:
     * 1- triggered nodes with fewer triggering in-scenario nodes leading up to them (smaller number first)
     * 2- the number of required outcomes (larger number first)
     * 3- their names (alphabetical) - connectors always come after parts
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
            public int compare( Node node, Node other ) {
                Collator collator = Collator.getInstance();
                // compare on transitive trigger count
                int nodeCount = node.transitiveTriggers().size();
                int otherCount = other.transitiveTriggers().size();
                int compTriggers = nodeCount == otherCount
                        ? 0
                        // fewer transitive triggers means comes before
                        : ( nodeCount < otherCount ? -1 : 1 );
                if ( compTriggers == 0 ) {
                    // if same trigger count, sort on importance to other nodes
                    nodeCount = node.requiredOutcomes().size();
                    otherCount = other.requiredOutcomes().size();
                    int compImportance = nodeCount == otherCount
                            ? 0
                            // more required outcomes means comes before
                            : ( nodeCount > otherCount ? -1 : 1 );
                    if ( compImportance == 0 ) {
                        // if same, sort on node name (connectors always come last)
                        return collator.compare(
                                node.isConnector() ? "\uFF5A\uFF5A" : ( (Part) node ).getTask(),
                                other.isConnector() ? "\uFF5A\uFF5A" : ( (Part) other ).getTask() );
                    } else {
                        return compImportance;
                    }
                } else
                    return compTriggers;
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
     * @param service the underlying store
     * @param actor   the actor for the new part
     * @param task    the task of the new part
     * @return the new part                                        he
     */
    public Part createPart( Service service, Actor actor, String task ) {
        Part result = service.createPart( this );
        result.setActor( actor );
        result.setTask( task );
        addNode( result );
        return result;
    }

    /**
     * Convenience accessor for tests.
     *
     * @param service the underlying store
     * @param role    the role for the new part
     * @param task    the task of the new part
     * @return the new part
     */
    public Part createPart( Service service, Role role, String task ) {
        Part result = service.createPart( this );
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
     * "Replace" remove node by connectors in flows to and from node.
     * Quietly succeeds if node is not part of the scenario
     *
     * @param node the node to remove.
     */
    public void removeNode( Node node ) {
        Service service = Project.service();
        if ( getNodeIndex().containsKey( node.getId() )
                && ( node.isConnector() || hasMoreThanOnePart() ) ) {

            Iterator<Flow> ins = node.requirements();
            while ( ins.hasNext() ) {
                Flow in = ins.next();
                // Preserve the outcome of the source the flow represents
                if ( in.isInternal()
                        && in.getSource().isPart()
                        && !in.getSource().hasMultipleOutcomes( in.getName() ) ) {
                    Flow flow = service.connect( in.getSource(), service.createConnector( this ), in.getName() );
                    flow.initFrom( in );
                }
                in.disconnect();
            }

            Iterator<Flow> outs = node.outcomes();
            while ( outs.hasNext() ) {
                Flow out = outs.next();
                if ( out.isInternal()
                        && out.getTarget().isPart()
                        && !out.getSource().hasMultipleRequirements( out.getName() ) ) {
                    Flow flow = service.connect( service.createConnector( this ), out.getTarget(), out.getName() );
                    flow.initFrom( out );
                }
                out.disconnect();
            }

            if ( node.isConnector() ) {
                Iterator<ExternalFlow> xf = ( (Connector) node ).externalFlows();
                while ( xf.hasNext() ) {
                    xf.next().disconnect();
                }
            }

            node.setScenario( null );
            nodeIndex.remove( node.getId() );
        }
    }

    private boolean hasMoreThanOnePart() {
        Iterator<Part> parts = parts();
        parts.next();
        // Note: scenario always has at least one part
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
     * @param id a long
     * @return a flow
     * @throws NotFoundException if not found
     */
    public Flow findFlow( long id ) throws NotFoundException {
        Flow flow = null;
        Iterator<Flow> flows = flows();
        while ( flow == null && flows.hasNext() ) {
            Flow f = flows.next();
            if (f.getId() == id) flow = f;
        }
        if (flow == null) throw new NotFoundException();
        else return flow;
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
            setIterators( nodeIterator.next() );
        }

        @SuppressWarnings( {"unchecked"} )
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
            return outcomeIterator.hasNext() ? outcomeIterator.next()
                    : reqIterator.next();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
