package com.mindalliance.channels.model;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * A segment of the plan.
 * Provides an iterator on its nodes.
 */
@Entity
public class Segment extends ModelObject {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( Segment.class );
    /**
     * The default name for new segments.
     */
    public static final String DEFAULT_NAME = "Untitled";

    /**
     * The default description for new segments.
     */
    public static final String DEFAULT_DESCRIPTION = "No description";

    /**
     * Initial node capacity.
     */
    private static final int INITIAL_CAPACITY = 20;

    /**
     * Nodes, indexed by id.
     */
    private Map<Long, Node> nodeIndex;
    /**
     * Plan event addressed to by this segment.
     * The event is always a type
     * (planning is done for types of events, not actual events since they have yet to occur).
     */
    private Event event;
    /**
     * Plan phase addressed by this segment.
     */
    private Phase phase;
    /**
     * Risks to be mitigated in this segment.
     */
    private List<Risk> risks = new ArrayList<Risk>();

    /**
     * The query service in charge of this segment.
     */
    private transient QueryService queryService;
    /**
     * Whether this segment is in the process of being deleted.
     * Used by xml export only.
     */
    private boolean beingDeleted = false;

    public Segment() {
        setNodeIndex( new HashMap<Long, Node>( INITIAL_CAPACITY ) );
    }

    @OneToMany( cascade = {CascadeType.REMOVE}, mappedBy = "segment" )
    @MapKey( name = "id" )
    Map<Long, Node> getNodeIndex() {
        return nodeIndex;
    }

    void setNodeIndex( Map<Long, Node> nodeIndex ) {
        this.nodeIndex = nodeIndex;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent( Event event ) {
        assert event.isType();
        this.event = event;
    }

    @OneToMany
    public List<Risk> getRisks() {
        return risks;
    }

    public void setRisks( List<Risk> risks ) {
        this.risks = risks;
    }

    /**
     * Add a risk.
     *
     * @param risk a risk
     */
    public void addRisk( Risk risk ) {
        risks.add( risk );
    }

    /**
     * Remove a risk.
     *
     * @param risk a risk
     */
    public void removeRisk( Risk risk ) {
        List<Part> mitigators = getMitigators( risk );
        for ( Part part : mitigators ) {
            part.getMitigations().remove( risk );
        }
        risks.remove( risk );
    }

    /**
     * Iterate over the nodes in this segment.
     * There should always be at least a node in the segment.
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
     * @return the number of nodes in this segment
     */
    @Transient
    public int getNodeCount() {
        return getNodeIndex().size();
    }

    /**
     * Convenience accessor for tests.
     *
     * @param queryService the underlying store
     * @param actor        the actor for the new part
     * @param task         the task of the new part
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
     * @param queryService the underlying store
     * @param role         the role for the new part
     * @param task         the task of the new part
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
     * Add a new node to this segment.
     *
     * @param node the new node
     */
    public void addNode( Node node ) {
        nodeIndex.put( node.getId(), node );
        node.setSegment( this );
    }

    /**
     * Remove a node from this segment.
     * Quietly succeeds if node is not part of the segment
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
                    toDisconnect.add( xf.next() );
                }
                // Avoid ConcurrentModificationException
                for ( ExternalFlow flow : toDisconnect ) {
                    flow.disconnect();
                }
            }

            queryService.remove( node );
            nodeIndex.remove( node.getId() );
            node.setSegment( null );
        }
    }

    private boolean hasMoreThanOnePart() {
        Iterator<Part> parts = parts();
        parts.next();
        // Note: segment must always have at least one part
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
     * Iterates over inputs of this segment.
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
     * Iterates over the parts of this segment.
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
     * Iterates over outputs of this segment.
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
     * Get an iterator on the flows of the segment, sorted alphabetically.
     *
     * @return an iterator on unique nodes
     */
    public Iterator<Flow> flows() {
        return new FlowIterator();
    }

    /**
     * Find node to display when none was specified.
     *
     * @return the first part in this segment
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
     * Find roles in a given organization used in this segment.
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
     * @throws com.mindalliance.channels.NotFoundException
     *          if not found
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
        if ( queryService == null ) {
            queryService = Channels.instance().getQueryService();
            LOG.warn( "Query service was not set" );
        }
        return queryService;
    }

    public void setQueryService( QueryService queryService ) {
        assert queryService != null;
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
     * {@inheritDoc}
     */
    public void beforeRemove( QueryService queryService ) {
        super.beforeRemove( queryService );
        queryService.getCurrentPlan().removeSegment( this );
    }

    /**
     * Get risk given type and name of organization.
     *
     * @param type             a risk type
     * @param organizationName a string
     * @return a risk or null if none matching
     */
    public Risk getRisk( final Risk.Type type, final String organizationName ) {
        return (Risk) CollectionUtils.find( risks, new Predicate() {
            public boolean evaluate( Object obj ) {
                Risk risk = (Risk) obj;
                return risk.getType() == type
                        && risk.getOrganization().getName().equals( organizationName );
            }
        } );
    }

    /**
     * Find all parts that mitigate a given risk.
     *
     * @param risk a risk
     * @return a list of parts
     */
    public List<Part> getMitigators( Risk risk ) {
        List<Part> mitigators = new ArrayList<Part>();
        Iterator<Part> parts = parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.getMitigations().contains( risk ) ) mitigators.add( part );
        }
        return mitigators;
    }

    /**
     * Get the list of all parts in the segment.
     *
     * @return a list of parts
     */
    @SuppressWarnings( "unchecked" )
    public List<Part> listParts() {
        return IteratorUtils.toList( parts() );
    }

    /**
     * Get the list of all flows in the segment.
     *
     * @return a list of flows
     */
    @SuppressWarnings( "unchecked" )
    public List<Flow> listFlows() {
        return IteratorUtils.toList( flows() );
    }


    /**
     * Get text about phase and event.
     *
     * @return a string
     */
    public String getPhaseEventTitle() {
        StringBuilder sb = new StringBuilder();
        sb.append( StringUtils.capitalize( getPhase().getName() ) );
        sb.append( ' ' );
        sb.append( getPhase().getPreposition() );
        sb.append( ' ' );
        sb.append( StringUtils.uncapitalize( getEvent().getName() ) );
        return sb.toString();
    }

    /**
     * Whether the segment can be initiated by a given part.
     * True if the part causes this segment's event and the segment phase is concurrent.
     * True if the part terminates a concurrent segment for same event and this segment is postEvent.
     *
     * @param part a part
     * @return a boolean
     */
    public boolean isInitiatedBy( Part part ) {
        return !initiationCause( part ).isEmpty();
    }

    /**
     * Explains why part initiates this segment.
     *
     * @param part a part
     * @return a string
     */
    public String initiationCause( Part part ) {
        Event initiatedEvent = part.getInitiatedEvent();
        if ( initiatedEvent != null && initiatedEvent.equals( getEvent() )
                && getPhase().isConcurrent() ) {
            return "causes event \"" + getEvent().getName().toLowerCase() + "\"";
        } else if ( part.isTerminatesEventPhase()
                && part.getSegment().getPhase().isConcurrent()
                && part.getSegment().getEvent().equals( getEvent() )
                && getPhase().isPostEvent() ) {
            return "terminates event \"" + getEvent().getName().toLowerCase() + "\"";
        } else {
            return "";
        }

    }

    /**
     * Whether the segment can be terminated by a given part.
     * True if the part explicitly terminates the phase event of this segment.
     * True if the part causes an event and the segment is for pre-event phase of that event.
     *
     * @param part a part
     * @return a boolean
     */
    public boolean isTerminatedBy( Part part ) {
        return !terminationCause( part ).isEmpty();
    }

    /**
     * Explains why part terminates this segment.
     *
     * @param part a part
     * @return a string
     */
    public String terminationCause( Part part ) {
        Event initiatedEvent = part.getInitiatedEvent();
        if ( part.getSegment().equals( this )
                && part.isTerminatesEventPhase() ) {
            return "terminates " + this.getPhaseEventTitle().toLowerCase();
        } else if ( initiatedEvent != null
                && initiatedEvent.equals( getEvent() )
                && getPhase().isPreEvent() ) {
            return "causes event \"" + initiatedEvent.getName().toLowerCase() + "\"";
        } else {
            return "";
        }

    }

    /**
     * List all connectors.
     *
     * @return a list of connectors
     */
    @SuppressWarnings( "unchecked" )
    public List<Connector> listConnectors() {
        return (List<Connector>) CollectionUtils.select(
                (List<Node>) IteratorUtils.toList( nodes() ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Node) obj ).isConnector();
                    }
                }
        );
    }

    /**
     * List all parts from other segments connected to this one via external flows.
     *
     * @return a list of parts
     */
    public List<Part> listExternalParts() {
        Set<Part> externalParts = new HashSet<Part>();
        for ( Connector connector : listConnectors() ) {
            Iterator<ExternalFlow> externalFlows = connector.externalFlows();
            while ( externalFlows.hasNext() ) {
                Part part = externalFlows.next().getPart();
                if ( !part.getSegment().equals( this ) ) {
                    externalParts.add( part );
                }
            }
        }
        return new ArrayList<Part>( externalParts );
    }

    /**
     * List all external flows.
     *
     * @return a list of external flows
     */
    public List<ExternalFlow> listExternalFlows() {
        List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
        for ( Connector connector : listConnectors() ) {
            Iterator<ExternalFlow> iter = connector.externalFlows();
            while ( iter.hasNext() ) externalFlows.add( iter.next() );
        }
        for ( Part part : listParts() ) {
            Iterator<Flow> outcomes = part.outcomes();
            while ( outcomes.hasNext() ) {
                Flow flow = outcomes.next();
                if ( flow.isExternal() ) externalFlows.add( (ExternalFlow) flow );
            }
            Iterator<Flow> requirements = part.requirements();
            while ( requirements.hasNext() ) {
                Flow flow = requirements.next();
                if ( flow.isExternal() ) externalFlows.add( (ExternalFlow) flow );
            }
        }
        return externalFlows;
    }

    /**
     * At least one risk ends with segment.
     * @return a boolean
     */
    public boolean hasTerminatingRisks() {
        return CollectionUtils.exists(
                getRisks(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ((Risk)object).isEndsWithSegment();
                    }
                }
        );
    }

    //=================================================
    /**
     * An iterator that walks through all flow in the segment.
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
            return outcomeIterator.hasNext() ?
                    outcomeIterator.next() : reqIterator.next();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Transient
    public boolean isBeingDeleted() {
        return beingDeleted;
    }

    public void setBeingDeleted( boolean beingDeleted ) {
        this.beingDeleted = beingDeleted;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean isLockable() {
        return false;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase( Phase phase ) {
        this.phase = phase;
    }

    /**
     * {@inheritDoc}
     */
    public boolean references( final ModelObject mo ) {
        return ModelObject.areIdentical( phase, mo )
                || ModelObject.areIdentical( event, mo )
                ||
                CollectionUtils.exists(
                        risks,
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                return ( (Risk) obj ).references( mo );
                            }
                        } );
    }
}
