package com.mindalliance.channels.model;

import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.lang.StringUtils;

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
public class Segment extends ModelObject {

    /**
     * The default name for new segments.
     */
    public static final String DEFAULT_NAME = "Untitled";

    /**
     * The default description for new segments.
     */
    public static final String DEFAULT_DESCRIPTION = "No description";

    // private static final long serialVersionUID = -595829017311913002L;

    /**
     * Initial node capacity.
     */
    private static final int INITIAL_CAPACITY = 20;

    /**
     * Nodes, indexed by id.
     */
    private final Map<Long, Node> nodeIndex = new HashMap<Long, Node>( INITIAL_CAPACITY );

    /**
     * Plan event addressed to by this segment.
     * The event is always a type
     * (planning is done for types of events, not actual events since they have yet to occur).
     */
    private Event event;

    /**
     * A qualifcation of the intensity of the event.
     */
    private Level eventLevel;

    /**
     * Plan phase addressed by this segment.
     */
    private Phase phase;

    /**
     * Goals (risk mitigations, gains to be made) for the segment.
     */
    private List<Goal> goals = new ArrayList<Goal>();

    /**
     * Whether this segment is in the process of being deleted.
     * Used by xml export only.
     */
    private boolean beingDeleted;

    public Segment() {
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent( Event event ) {
        assert event.isType();
        this.event = event;
    }

    public Level getEventLevel() {
        return eventLevel;
    }

    public void setEventLevel( Level eventLevel ) {
        this.eventLevel = eventLevel;
    }

    /**
     * Get goals that are risk mitigations.
     *
     * @return a list of goals
     */
    @SuppressWarnings( "unchecked" )
    public List<Goal> getRisks() {
        return (List<Goal>) CollectionUtils.select(
                getGoals(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        Goal goal = (Goal) object;
                        return goal.isRiskMitigation();
                    }
                }
        );
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals( List<Goal> goals ) {
        this.goals = goals;
    }

    /**
     * Add a goal.
     *
     * @param goal a goal
     */
    public void addGoal( Goal goal ) {
        goals.add( goal );
    }

    /**
     * Remove a goal.
     *
     * @param goal a goal
     */
    public void removeGoal( Goal goal ) {
        List<Part> achievers = getAchievers( goal );
        for ( Part part : achievers ) {
            part.getGoals().remove( goal );
        }
        goals.remove( goal );
    }


    /**
     * Iterate over the nodes in this segment.
     * There should always be at least a node in the segment.
     * The nodes are sorted on their tasks.
     *
     * @return an iterator on sorted nodes
     */
    public Iterator<Node> nodes() {
        List<Node> nodes = new ArrayList<Node>( nodeIndex.values() );
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
    public int getNodeCount() {
        return nodeIndex.size();
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
     * @param node    the node to remove.
     * @param planDao the dao
     */
    public void removeNode( Node node, Memory planDao ) {
        if ( nodeIndex.containsKey( node.getId() )
                && ( node.isConnector() || hasMoreThanOnePart() ) ) {
            Iterator<Flow> ins = node.receives();
            while ( ins.hasNext() ) {
                ins.next().disconnect( planDao );
            }
            Iterator<Flow> outs = node.sends();
            while ( outs.hasNext() ) {
                outs.next().disconnect( planDao );
            }

            if ( node.isConnector() ) {
                List<ExternalFlow> toDisconnect = new ArrayList<ExternalFlow>();
                Iterator<ExternalFlow> xf = ( (Connector) node ).externalFlows();
                while ( xf.hasNext() ) {
                    toDisconnect.add( xf.next() );
                }
                // Avoid ConcurrentModificationException
                for ( ExternalFlow flow : toDisconnect ) {
                    flow.disconnect( planDao );
                }
            }
            planDao.remove( node );
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
     *
     * @param planDao
     */
    public void disconnect( Memory planDao ) {
        for ( Node n : nodeIndex.values() ) {
            if ( n.isConnector() )
                ( (Connector) n ).disconnect( planDao );
        }

    }

    /**
     * Iterates over inputs of this segment.
     *
     * @return an iterator on connectors having sends
     */
    @SuppressWarnings( {"unchecked"} )
    public Iterator<Connector> inputs() {
        return (Iterator<Connector>) new FilterIterator( nodes(), new Predicate() {
            public boolean evaluate( Object object ) {
                Node n = (Node) object;
                return n.isConnector() && n.sends().hasNext();
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
     * @return an iterator on connectors having receives
     */
    @SuppressWarnings( {"unchecked"} )
    public Iterator<Connector> outputs() {
        return (Iterator<Connector>) new FilterIterator( nodes(), new Predicate() {
            public boolean evaluate( Object object ) {
                Node n = (Node) object;
                return n.isConnector() && n.receives().hasNext();
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
            if ( part.isInOrganization( organization ) && part.getActor() == null && part.getRole() == null )
                partsForRole.add( part );
        }
        return partsForRole;
    }

    /**
     * Find parts played by a given role in a given organization.
     *
     * @param organization the organization, possibly Organization.UNKNOWN
     * @param role         the role, possibly Role.UNKNOWN
     * @param jurisdiction
     * @return the appropriate parts
     */
    public List<Part> findParts( Organization organization, Role role, Place jurisdiction ) {
        List<Part> partsForRole = new ArrayList<Part>();
        Iterator<Part> parts = parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.isInOrganization( organization )
                    && part.isPlayedBy( role )
                    && ( jurisdiction == null || part.isInJurisdiction( jurisdiction ) ) )
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
            if ( part.isInOrganization( organization ) ) {
                if ( part.getRole() == null ) {
                    hasUnknown = true;
                } else {
                    roles.add( part.getRole() );
                }
            }
        }

        List<Role> list = new ArrayList<Role>( roles );
        Collections.sort( list, new Comparator<Role>() {
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
     * @throws NotFoundException if not found
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

    public QueryService getQueryService() {
        return Channels.instance().getQueryService();
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
    @Override
    public void beforeRemove( QueryService queryService ) {
        super.beforeRemove( queryService );
        queryService.getCurrentPlan().removeSegment( this );
    }

    /**
     * Get goal given category, positiveness and name of organization.
     *
     * @param category a goal category
     * @param positive whether the goal is positive
     * @param orgName  a string
     * @return a goal or null if none matching
     */
    public Goal getGoal(
            final Goal.Category category, final boolean positive, final String orgName ) {

        return (Goal) CollectionUtils.find( goals, new Predicate() {
            public boolean evaluate( Object object ) {
                Goal goal = (Goal) object;
                return goal.getCategory() == category
                        && goal.isPositive() == positive
                        && orgName.equals( goal.getOrganization().getName() );
            }
        } );
    }


    /**
     * Find all parts that mitigate a given risk.
     *
     * @param goal a goal
     * @return a list of parts
     */
    public List<Part> getAchievers( Goal goal ) {
        List<Part> achievers = new ArrayList<Part>();
        Iterator<Part> parts = parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.getGoals().contains( goal ) ) achievers.add( part );
        }
        return achievers;
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
        sb.append( StringUtils.capitalize( phase.getName() ) );
        sb.append( ' ' );
        sb.append( phase.getPreposition() );
        sb.append( ' ' );
        sb.append( StringUtils.uncapitalize( event.getName() ) );
        if ( eventLevel != null ) {
            sb.append( " (" );
            sb.append( eventLevel.getLabel() );
            sb.append( ')' );
        }
        return sb.toString();
    }

    /**
     * Whether the segment can be initiated by a given part.
     * True if the part causes this segment's event and the segment phase is concurrent.
     * True if the part terminates a concurrent segment for same event and this segment is
     * postEvent.
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
        Event initiator = part.getInitiatedEvent();
        if ( event.equals( initiator ) && phase.isConcurrent() )
            return "causes event \"" + event.getName().toLowerCase() + '\"';
        else {
            Segment partSegment = part.getSegment();
            return part.isTerminatesEventPhase()
                    && partSegment.getPhase().isConcurrent()
                    && event.equals( partSegment.getEvent() )
                    && phase.isPostEvent() ?
                    "terminates event \"" + event.getName().toLowerCase() + '\"'
                    : "";
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
        Event initiator = part.getInitiatedEvent();
        return equals( part.getSegment() ) && part.isTerminatesEventPhase() ?
                "terminates " + getPhaseEventTitle().toLowerCase()
                : event.equals( initiator ) && getPhase().isPreEvent() ?
                "causes event \"" + initiator.getName().toLowerCase() + '\"'
                : "";
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
                    public boolean evaluate( Object object ) {
                        return ( (Node) object ).isConnector();
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
                if ( !equals( part.getSegment() ) ) {
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
            Iterator<Flow> sends = part.sends();
            while ( sends.hasNext() ) {
                Flow flow = sends.next();
                if ( flow.isExternal() ) externalFlows.add( (ExternalFlow) flow );
            }
            Iterator<Flow> receives = part.receives();
            while ( receives.hasNext() ) {
                Flow flow = receives.next();
                if ( flow.isExternal() ) externalFlows.add( (ExternalFlow) flow );
            }
        }
        return externalFlows;
    }

    /**
     * At least one risk ends with segment.
     *
     * @return a boolean
     */
    public boolean hasTerminatingRisks() {
        return CollectionUtils.exists(
                getGoals(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        Goal goal = (Goal) object;
                        return goal.isRiskMitigation() && goal.isEndsWithSegment();
                    }
                }
        );
    }

    /**
     * Display string with maximum length.
     *
     * @param maxLength an int
     * @return a string
     */
    public String displayString( int maxLength ) {
        return StringUtils.abbreviate( getName(), maxLength );
    }


    //=================================================
    /**
     * An iterator that walks through all flow in the segment.
     * This is done by iterating on all send flows of all nodes,
     * in node-order.
     */
    private final class FlowIterator implements Iterator<Flow> {

        /**
         * Iterator on nodes.
         */
        private final Iterator<Node> nodeIterator;

        /**
         * Iterator on the sends of the current node.
         */
        private Iterator<Flow> sendIterator;

        /**
         * Iterator on the external receives of the current node.
         */
        private Iterator<Flow> reqIterator;

        private FlowIterator() {
            nodeIterator = nodes();
            if ( nodeIterator.hasNext() )
                setIterators( nodeIterator.next() );
            else {
                sendIterator = Collections.<Flow>emptyList().iterator();
                reqIterator = Collections.<Flow>emptyList().iterator();
            }
        }

        @SuppressWarnings( {"unchecked"} )
        private void setIterators( Node node ) {
            sendIterator = node.sends();
            reqIterator = (Iterator<Flow>) new FilterIterator(
                    node.receives(),
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return !( (Flow) object ).isInternal();
                        }
                    } );
        }

        public boolean hasNext() {
            while ( !sendIterator.hasNext() && !reqIterator.hasNext() && nodeIterator.hasNext() )
                setIterators( nodeIterator.next() );

            return sendIterator.hasNext() || reqIterator.hasNext();
        }

        public Flow next() {
            if ( !hasNext() )
                throw new NoSuchElementException();
            return sendIterator.hasNext() ?
                    sendIterator.next() : reqIterator.next();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public boolean isBeingDeleted() {
        return beingDeleted;
    }

    public void setBeingDeleted( boolean beingDeleted ) {
        this.beingDeleted = beingDeleted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public boolean references( final ModelObject mo ) {
        return ModelObject.areIdentical( phase, mo )
                || ModelObject.areIdentical( event, mo )
                || CollectionUtils.exists(
                goals,
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (Goal) object ).references( mo );
                    }
                } );
    }
}
