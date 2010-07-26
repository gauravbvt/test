package com.mindalliance.channels.model;

import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.nlp.Matcher;
import org.apache.commons.collections.iterators.IteratorChain;

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
public abstract class Node extends ModelObject implements SegmentObject {

    /**
     * The name for new flows.
     */
    public static final String DEFAULT_FLOW_NAME = "";

    /**
     * Initial capacity of send and receive flows.
     */
    private static final int INITIAL_CAPACITY = 5;

    /**
     * All receives, indexed by id.
     */
    private Map<Long, Flow> receives;

    /**
     * All sends, indexed by id.
     */
    private Map<Long, Flow> sends;

    /**
     * The unique segment containing this node.
     */
    private Segment segment;

    protected Node() {
        sends = new HashMap<Long, Flow>();
        receives = new HashMap<Long, Flow>();
    }

    /**
     * Get a long string that can be used as a title for this node.
     *
     * @return a generated short description
     */
    public abstract String getTitle();

    /**
     * Find a flow connected to this node, given its id.
     *
     * @param id the id
     * @return a flow or null
     */
    public Flow getFlow( long id ) {
        Flow result = receives.get( id );
        if ( result == null )
            result = sends.get( id );

        return result;
    }

    protected Map<Long, Flow> getSends() {
        return sends;
    }

    /**
     * Set sends, rebuilding the index.
     * Package-visible for tests.
     *
     * @param sends the new sends
     */
    void setSends( Map<Long, Flow> sends ) {
        this.sends = sends;
    }

    /**
     * Iterates over sends, alphabetically by print string.
     *
     * @return a flow iterator
     */
    public Iterator<Flow> sends() {
        List<Flow> flows = new ArrayList<Flow>();
        flows.addAll( getSends().values() );
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
     * Add a send to this node.
     *
     * @param send the send
     */
    public void addSend( Flow send ) {
        sends.put( send.getId(), send );
        send.setSource( this );
    }

    /**
     * Remove an send from this node.
     * Removes the source node if a connector.
     *
     * @param send the send
     */
    public void removeSend( Flow send ) {
        sends.remove( send.getId() );
        send.setSource( null );
    }

    protected Map<Long, Flow> getReceives() {
        return receives;
    }


    /**
     * Iterates over receives, alphabetically by print string.
     *
     * @return a flow iterator
     */
    public Iterator<Flow> receives() {
        List<Flow> flows = new ArrayList<Flow>();
        flows.addAll( getReceives().values() );
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
     * Add a receive to this node.
     *
     * @param receive the receive
     */
    public void addReceive( Flow receive ) {
        receives.put( receive.getId(), receive );
        receive.setTarget( this );
    }

    /**
     * Remove a receive from this node.
     * Removes the target node if a connector.
     *
     * @param receive the receive
     */
    public void removeReceive( Flow receive ) {
        receives.remove( receive.getId() );
        receive.setTarget( null );
    }

    /**
     * Get all flows attached to this part, receives and sends.
     *
     * @return a flow iterator
     */
    @SuppressWarnings( {"unchecked"} )
    public Iterator<Flow> flows() {
        return (Iterator<Flow>) new IteratorChain( receives(), sends() );
    }

    public boolean isPart() {
        return false;
    }

    public boolean isConnector() {
        return false;
    }

    public Segment getSegment() {
        return segment;
    }

    /**
     * {@inheritDoc}
     */
    public List<Flow> getEssentialFlows( boolean ssumeFails, QueryService queryService ) {
        return new ArrayList<Flow>();
    }

    public void setSegment( Segment segment ) {
        this.segment = segment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getTitle();
    }

   /**
     * Test if this node is connected to another node by a flow.
     *
     * @param send test if node is a send, otherwise a receive
     * @param node the other node
     * @return true if connected.
     */
    public boolean isConnectedTo( boolean send, Node node ) {
        boolean result = false;
        Map<Long, Flow> flows = send ? sends : receives;
        for ( Iterator<Flow> it = flows.values().iterator(); !result && it.hasNext(); ) {
            Flow f = it.next();
            result = f.isConnectedTo( send, node );
        }

        return result;
    }


   /**
     * Test if this node is connected to another node by a flow of a given name.
     *
     * @param send test if node is a send, otherwise a receive
     * @param node the other node
     * @param name the name of the flow
     * @return true if connected.
     */
    public boolean isConnectedTo( boolean send, Node node, String name ) {
        boolean result = false;
        Map<Long, Flow> flows = send ? sends : receives;
        for ( Iterator<Flow> it = flows.values().iterator(); !result && it.hasNext(); ) {
            Flow f = it.next();
            result = name.equals( f.getName() ) && f.isConnectedTo( send, node );
        }

        return result;
    }

    /**
     * Find all send flows that are required.
     *
     * @return a list of flows
     */
    public List<Flow> requiredSends() {
        List<Flow> requiredFlows = new ArrayList<Flow>();
        for ( Flow out : sends.values() ) {
            if ( out.isRequired() ) {
                requiredFlows.add( out );
            }
        }
        return requiredFlows;
    }

    /**
     * Whether this node has more than one send of a given name
     *
     * @param name the name of a flow
     * @return a boolean
     */
    public boolean hasMultipleSends( String name ) {
        int count = 0;
        for ( Flow send : sends.values() ) {
            if ( Matcher.getInstance().same( send.getName(), name ) ) count++;
        }
        return count > 1;
    }

    /**
     * Whether this node has more than one receive of a given name
     *
     * @param name the name of a flow
     * @return a boolean
     */
    public boolean hasMultipleReceives( String name ) {
        int count = 0;
        for ( Flow receive : receives.values() ) {
            if ( Matcher.getInstance().same( receive.getName(), name ) ) count++;
        }
        return count > 1;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndefined() {
        return super.isUndefined() && receives.isEmpty() && sends.isEmpty();
    }

    /**
     * Make display string.
     * @return  a string
     */
    abstract public String displayString();

    /**
     * Make abbreviated  string.
     * @param maxItemLength an int
     * @return  a string
     */
    abstract public String displayString( int maxItemLength );

    /**
     * Make abbreviated  string including info name if connector..
     * @param maxItemLength an int
     * @return  a string
     */
    abstract public String fullDisplayString( int maxItemLength );
}
