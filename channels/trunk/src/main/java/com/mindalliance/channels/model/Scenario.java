package com.mindalliance.channels.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A scenario in the project.
 */
public class Scenario extends NamedObject {

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
            throw new IllegalArgumentException( "Node list is not empty" );

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
        getNodes().remove( node );
        nodeIndex.remove( node.getId() );
    }

    /**
     * Get a node, given its id.
     * @param id the id
     * @return the node, or null if not found
     */
    public Node getNode( long id ) {
        return nodeIndex.get( id );
    }
}
