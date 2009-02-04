package com.mindalliance.channels;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
/**
 * A scenario in the project.
 * Provides an iterator on its nodes.
 */
@Entity
public class Scenario extends ModelObject {

    // TODO - Add location (as in area) of the scenario (where the scenario applies - e.g. avian flu case in New Jersey)

    /** The default name for new scenarios. */
    public static final String DEFAULT_NAME = "Untitled";

    /** The default description for new scenarios. */
    public static final String DEFAULT_DESCRIPTION = "A scenario";

    /** Initial node capacity. */
    private static final int INITIAL_CAPACITY = 20;

    /** Nodes in the flow graph. */
    private Set<Node> nodes = new HashSet<Node>( INITIAL_CAPACITY );

    /** Nodes, indexed by id. */
    private Map<Long,Node> nodeIndex = new HashMap<Long,Node>( INITIAL_CAPACITY );

    public Scenario() {
    }

    @OneToMany( cascade = CascadeType.ALL )
    @Cascade( org.hibernate.annotations.CascadeType.DELETE_ORPHAN )
    private Set<Node> getNodes() {
        return nodes;
    }

    /**
     * Set the nodes in this scenario.
     * @param nodes the nodes
     * @throws IllegalArgumentException when list is empty
     */
    protected void setNodes( Set<Node> nodes ) {
        if ( nodes.isEmpty() )
            throw new IllegalArgumentException();

        for ( Node node : getNodes() )
            node.setScenario( null );

        this.nodes = new HashSet<Node>( nodes );
        nodeIndex = new HashMap<Long,Node>( INITIAL_CAPACITY );
        for ( Node node: nodes ) {
            addNode( node );
        }
    }

    /**
     * Iterate over the nodes in this scenario.
     * There should always be at least a node in the scenario.
     * @return an iterator on nodes
     * */
    public Iterator<Node> nodes() {
        // TODO should nodes be sorted here?
        return new TreeSet<Node>( getNodes() ).iterator();
    }

    /**
     * @return the number of nodes in this scenario
     */
    @Transient
    public int getNodeCount() {
        return getNodes().size();
    }

    /**
     * Convenience accessor for tests.
     * @param service the underlying store
     * @param actor the actor for the new part
     * @param task the task of the new part
     * @return the new part
     */
    public Part createPart( Service service, Actor actor, String task ) {
        final Part result = service.createPart( this );
        result.setActor( actor );
        result.setTask( task );
        addNode( result );
        return result;
    }

    /**
     * Convenience accessor for tests.
     * @param service the underlying store
     * @param role the role for the new part
     * @param task the task of the new part
     * @return the new part
     */
    public Part createPart( Service service, Role role, String task ) {
        final Part result = service.createPart( this );
        result.setRole( role );
        result.setTask( task );
        addNode( result );
        return result;
    }

    /**
     * Add a new node to this scenario.
     * @param node the new node
     */
    public void addNode( Node node ) {
        getNodes().add( node );
        nodeIndex.put( node.getId(), node );
        node.setScenario( this );
    }

    /**
     * Remove a node from this scenario.
     * Quietly succeeds if node is not part of the scenario
     * @param node the node to remove.
     */
    public void removeNode( Node node ) {
        if ( getNodes().contains( node ) && ( node.isConnector() || hasMoreThanOnePart() ) ) {
            getNodes().remove( node );
            nodeIndex.remove( node.getId() );

            final Iterator<Flow> ins = node.requirements();
            while ( ins.hasNext() )
                ins.next().disconnect();

            final Iterator<Flow> outs = node.outcomes();
            while ( outs.hasNext() )
                outs.next().disconnect();

            if ( node.isConnector() ) {
                final Iterator<ExternalFlow> xf = ( (Connector) node ).externalFlows();
                while ( xf.hasNext() ) {
                    xf.next().disconnect();
                }
            }

            node.setScenario( null );
        }
    }

    private boolean hasMoreThanOnePart() {
        final Iterator<Part> parts = parts();
        parts.next();
        // Note: scenario always has at least one part
        return parts.hasNext();
    }

    /**
     * Get a node, given its id.
     * @param id the id
     * @return the node, or null if not found
     */
    public Node getNode( long id ) {
        return nodeIndex.get( id );
    }

    /**
     * Iterates over inputs of this scenario.
     * @return an iterator on connectors having outcomes
     */
    @SuppressWarnings( { "unchecked" } )
    public Iterator<Connector> inputs() {
        return (Iterator<Connector>) new FilterIterator( nodes(), new Predicate() {
            public boolean evaluate( Object o ) {
                final Node n = (Node) o;
                return n.isConnector() && n.outcomes().hasNext();
            }
        } );
    }

    /**
     * Iterates over the parts of this scenario.
     * @return an iterator on parts
     */
    @SuppressWarnings( { "unchecked" } )
    public Iterator<Part> parts() {
        return (Iterator<Part>) new FilterIterator( nodes(), new Predicate() {
            public boolean evaluate( Object o ) {
                final Node n = (Node) o;
                return n.isPart();
            }
        } );
    }

    /**
     * Iterates over outputs of this scenario.
     * @return an iterator on connectors having requirements
     */
    @SuppressWarnings( { "unchecked" } )
    public Iterator<Connector> outputs() {
        return (Iterator<Connector>) new FilterIterator( nodes(), new Predicate() {
            public boolean evaluate( Object o ) {
                final Node n = (Node) o;
                return n.isConnector() && n.requirements().hasNext();
            }
        } );
    }

    /**
     * Get an iterator on the flows of the scenario, sorted alphabetically.
     * @return an iterator on unique nodes
     */
    public Iterator<Flow> flows() {
        return new FlowIterator();
    }

    /**
     * Find node to display when none was specified.
     * @return the first part in this scenario
     */
    @Transient
    public Part getDefaultPart() {
        return parts().next();
    }

    //=================================================
    /**
     * An iterator that walks through all flow in the scenario.
     * This is done by iterating on all outcome flows of all nodes,
     * in node-order.
     */
    private final class FlowIterator implements Iterator<Flow> {

        /** Iterator on nodes. */
        private final Iterator<Node> nodeIterator;

        /** Iterator on the outcomes of the current node. */
        private Iterator<Flow> outcomeIterator;

        /** Iterator on the external requirements of the current node. */
        private Iterator<Flow> reqIterator;

        private FlowIterator() {
            nodeIterator = nodes();
            setIterators( nodeIterator.next() );
        }

        @SuppressWarnings( { "unchecked" } )
        private void setIterators( Node node ) {
            outcomeIterator = node.outcomes();
            reqIterator = (Iterator<Flow>) new FilterIterator(
                    node.requirements(),
                    new Predicate() {
                        public boolean evaluate( Object o ) {
                            return !( (Flow) o ).isInternal();
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
