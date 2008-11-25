package com.mindalliance.channels.model;

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
public class Scenario extends ModelObject {

    // TODO - Add location (as in area) of the scenario (where the scenario applies - e.g. avian flu case in New Jersey)

    /** The default name for new scenarios. */
    static final String DEFAULT_NAME = "Untitled";

    /** The default description for new scenarios. */
    static final String DEFAULT_DESCRIPTION = "A scenario";

    /** Initial node capacity. */
    private static final int INITIAL_CAPACITY = 20;

    /** Nodes in the flow graph. */
    private Set<Node> nodes;

    /** Nodes, indexed by id. */
    private Map<Long,Node> nodeIndex;

    public Scenario() {
        initializeScenario( this );
    }

    /**
     * Utility constructor for tests.
     * @param name the name of the new object
     */
    public Scenario( String name ) {
        super( name );
    }

    /**
     * Create a new "empty" default project.
     * @return something to kick start the editing process
     */
    public static Scenario createDefault() {
        final Scenario result = new Scenario();
        initializeScenario( result );
        return result;
    }

    /**
     * Initialize a scenario to default project settings.
     * @param scenario the scenario to initialize
     */
    private static void initializeScenario( Scenario scenario ) {
        scenario.setName( DEFAULT_NAME );
        scenario.setDescription( DEFAULT_DESCRIPTION );

        final Set<Node> nodes = new HashSet<Node>( INITIAL_CAPACITY );

        nodes.add( new Part() );
        scenario.setNodes( nodes );
    }

    private Set<Node> getNodes() {
        return nodes;
    }

    /**
     * Set the nodes in this scenario.
     * @param nodes the nodes
     * @throws IllegalArgumentException when list is empty
     */
    protected final void setNodes( Set<Node> nodes ) {
        if ( nodes.isEmpty() )
            throw new IllegalArgumentException();

        this.nodes = new TreeSet<Node>( nodes );
        nodeIndex = new HashMap<Long,Node>( INITIAL_CAPACITY );
        for ( Node node: nodes ) {
            nodeIndex.put( node.getId(), node );
        }
    }

    /**
     * Iterate over the nodes in this scenario.
     * There should always be at least a node in the scenario.
     * @return an iterator on nodes
     * */
    public Iterator<Node> nodes() {
        return getNodes().iterator();
    }

    /**
     * Add a node to this scenario.
     * @param node the new node
     */
    public void addNode( Node node ) {
        getNodes().add( node );
        nodeIndex.put( node.getId(), node );
    }

    /**
     * Remove a node from this scenario.
     * Quietly succeeds if node is not part of the scenario
     * @param node the node to remove.
     */
    public void removeNode( Node node ) {
        if ( nodeIndex.size() > 1 ) {
            getNodes().remove( node );
            nodeIndex.remove( node.getId() );
        }
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
     * Create a flow between two nodes in the scenario.
     * @param source the source node.
     * @param target the source node.
     * @return a new flow.
     * @throws IllegalArgumentException when nodes are already connected or nodes are not both
     * in this scenario
     */
    public Flow connect( Node source, Node target ) {
        if ( getFlow( source, target ) != null )
                throw new IllegalArgumentException();

        final Flow result = new Flow();
        source.addOutcome( result );
        target.addRequirement( result );

        return result;
    }

    /**
     * Disconnect two nodes. No effect if nodes are not connected.
     * @param source a source node
     * @param target a target node
     */
    public void disconnect( Node source, Node target ) {
        final Flow flow = getFlow( source, target );
        if ( flow != null ) {
            source.removeOutcome( flow );
            target.removeRequirement( flow );
        }
    }

    /**
     * Find flow between a source and a target.
     * @param source the source
     * @param target the target
     * @return the connecting flow, or null if none
     * @throws IllegalArgumentException when nodes are not both in this scenario
     */
    private Flow getFlow( Node source, Node target ) {
        if ( !nodes.contains( source ) || !nodes.contains( target ) )
            throw new IllegalArgumentException();

        for ( Iterator<Flow> flows = source.outcomes(); flows.hasNext(); ) {
            final Flow f = flows.next();
            if ( target.equals( f.getTarget() ) )
                return f;
        }

        return null;
    }

    /**
     * Get an iterator on the flows of the scenario, sorted alphabetically.
     * @return an iterator on unique nodes
     */
    public Iterator<Flow> flows() {
        return new FlowIterator();
    }

    //=================================================
    /**
     * An iterator that walks through all flow in the scenario.
     * This is done by iterating on all outcome flows of all nodes,
     * in node-order.
     */
    private final class FlowIterator implements Iterator<Flow> {

        /** Iterator on nodes. */
        private final Iterator<Node> nodeIterator = nodes();

        /** Iterator on the outcomes of the current node. */
        private Iterator<Flow> outcomeIterator = nodeIterator.next().outcomes();

        private FlowIterator() {
        }

        public boolean hasNext() {
            while ( !outcomeIterator.hasNext() && nodeIterator.hasNext() )
                outcomeIterator = nodeIterator.next().outcomes();
            return outcomeIterator.hasNext();
        }

        public Flow next() {
            if ( !hasNext() )
                throw new NoSuchElementException();
            return outcomeIterator.next();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
