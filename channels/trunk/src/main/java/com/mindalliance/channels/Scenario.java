package com.mindalliance.channels;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;

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

    /** The dao responsible for this scenario. */
    private Dao dao;

    public Scenario() {
        initializeScenario( this );
    }

    /**
     * Initialize a scenario to default project settings.
     * @param scenario the scenario to initialize
     */
    public static void initializeScenario( Scenario scenario ) {
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
    public int getNodeCount() {
        return getNodes().size();
    }

    /**
     * Create and add a new part to this scenario.
     * @return the new part
     */
    public Part createPart() {
        final Part node = getDao().createPart();
        addNode( node );
        return node;
    }

    /**
     * Convenience accessor for tests.
     * @param actor the actor for the new part
     * @param task the task of the new part
     * @return the new part
     */
    public Part createPart( Actor actor, String task ) {
        final Part result = createPart();
        result.setActor( actor );
        result.setTask( task );

        return result;
    }

    /**
     * Convenience accessor for tests.
     * @param role the role for the new part
     * @param task the task of the new part
     * @return the new part
     */
    public Part createPart( Role role, String task ) {
        final Part result = createPart();
        result.setRole( role );
        result.setTask( task );

        return result;
    }

    /**
     * Create and add a new connector to this scenario.
     * @return the new connector
     */
    Connector createConnector() {
        final Connector node = getDao().createConnector();
        addNode( node );
        return node;
    }

    /**
     * Add a new node to this scenario.
     * @param node the new node
     */
    private void addNode( Node node ) {
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
            // TODO move to dao?
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
     * Create a flow between two nodes in this scenario, or between a node in this scenario and a
     * connector in another scenario.
     * @param source the source node.
     * @param target the target node.
     * @return a new flow.
     * @throws IllegalArgumentException when nodes are already connected or nodes are not both
     * in this scenario, or one of the node isn't a connector in a different scenario.
     */
    public Flow connect( Node source, Node target ) {
        final Flow result;

        if ( isInternal( source, target ) ) {
            if ( getFlow( source, target ) != null )
                    throw new IllegalArgumentException();

            result = new InternalFlow( source, target );
            source.addOutcome( result );
            target.addRequirement( result );

        } else if ( isExternal( source, target ) ) {
            result = new ExternalFlow( source, target );
            if ( source.isConnector() ) {
                target.addRequirement( result );
                ( (Connector) source ).addExternalFlow( (ExternalFlow) result );
            } else {
                source.addOutcome( result );
                ( (Connector) target ).addExternalFlow( (ExternalFlow) result );
            }

        } else
            throw new IllegalArgumentException();

        return result;
    }

    private boolean isInternal( Node source, Node target ) {
        final Scenario scenario = source.getScenario();
        return equals( scenario )
            && scenario.equals( target.getScenario() );
    }

    private boolean isExternal( Node source, Node target ) {
        final boolean targetIsConnector = equals( source.getScenario() )
                                          && !equals( target.getScenario() )
                                          && target.isConnector();
        final boolean sourceIsConnector = !equals( source.getScenario() )
                                          && equals( target.getScenario() )
                                          && source.isConnector();
        return targetIsConnector || sourceIsConnector;
    }

    /**
     * Find flow between a source and a target.
     * @param source the source
     * @param target the target
     * @return the connecting flow, or null if none
     */
    private static Flow getFlow( Node source, Node target ) {
        for ( Iterator<Flow> flows = source.outcomes(); flows.hasNext(); ) {
            final Flow f = flows.next();
            if ( target.equals( f.getTarget() ) )
                return f;
        }

        return null;
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
    public Part getDefaultPart() {
        return parts().next();
    }

    public final Dao getDao() {
        return dao;
    }

    public final void setDao( Dao dao ) {
        this.dao = dao;
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
            reqIterator = (Iterator<Flow>) new FilterIterator( node.requirements(), new Predicate() {
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
