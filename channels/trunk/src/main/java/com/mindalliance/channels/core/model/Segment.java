package com.mindalliance.channels.core.model;

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
     * Event phase addressed in this segment.
     */
    private EventPhase eventPhase = new EventPhase();
    /**
     * Event phases in context.
     */
    private List<EventTiming> context = new ArrayList<EventTiming>();

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

    public EventPhase getEventPhase() {
        return eventPhase;
    }

    public void setEventPhase( EventPhase eventPhase ) {
        this.eventPhase = eventPhase;
    }

    public List<EventTiming> getContext() {
        return context;
    }

    public void setContext( List<EventTiming> context ) {
        this.context = context;
    }

    @Override
    public String getKindLabel() {
        return "Template segment";
    }

    public Event getEvent() {
        return eventPhase.getEvent();
    }

    public void setEvent( Event event ) {
        eventPhase.setEvent( event );
    }

    public Level getEventLevel() {
        return eventPhase.getEventLevel();
    }

    public void setEventLevel( Level eventLevel ) {
        eventPhase.setEventLevel( eventLevel );
    }

    /**
     * Get goals that are risk mitigations.
     *
     * @return a list of goals
     */
    @SuppressWarnings( "unchecked" )
    public List<Goal> getRisks() {
        return (List<Goal>) CollectionUtils.select( goals,
                new Predicate() {
                    @Override
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
            @Override
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
     * Add a new node to this segment.
     *
     * @param node the new node
     * @return the node
     */
    public <T extends Node> T addNode( T node ) {
        //       assert !nodeIndex.containsKey( node.getId() );
        nodeIndex.put( node.getId(), node );
        node.setSegment( this );
        return node;
    }

    /**
     * Direct removal of a node.
     * <b>Note:</b> Use dao.removeNode( segment, node ) instead...
     *
     * @param node the node
     */
    public void removeNode( Node node ) {
        nodeIndex.remove( node.getId() );
        node.setSegment( null );
    }

    public boolean hasMoreThanOnePart() {
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
     * Iterates over inputs of this segment.
     *
     * @return an iterator on connectors having sends
     */
    @SuppressWarnings( {"unchecked"} )
    public Iterator<Connector> inputs() {
        return (Iterator<Connector>) new FilterIterator( nodes(), new Predicate() {
            @Override
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
            @Override
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
            @Override
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
     * @param locale       the default location
     * @return the appropriate parts
     */
    public List<Part> findParts( Organization organization, Place locale ) {
        List<Part> partsForRole = new ArrayList<Part>();
        Iterator<Part> parts = parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.isForOrganization( organization, locale ) && part.getActor() == null && part.getRole() == null )
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
     * @param locale       the default location
     * @return the appropriate parts
     */
    public List<Part> findParts(
            Organization organization, Role role, Place jurisdiction, Place locale ) {
        List<Part> partsForRole = new ArrayList<Part>();
        Iterator<Part> parts = parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.isForOrganization( organization, locale )
                    && part.isPlayedBy( role )
                    && ( jurisdiction == null || part.isInJurisdiction( jurisdiction, locale ) ) )
                partsForRole.add( part );
        }
        return partsForRole;
    }

    /**
     * Find roles in a given organization used in this segment.
     *
     * @param organization the organization, possibly Organization.UNKNOWN
     * @param locale       the default location
     * @return the appropriate roles.
     */
    public List<Role> findRoles( Organization organization, Place locale ) {
        boolean hasUnknown = false;

        Set<Role> roles = new HashSet<Role>();
        Iterator<Part> parts = parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.isForOrganization( organization, locale ) ) {
                if ( part.getRole() == null ) {
                    hasUnknown = true;
                } else {
                    roles.add( part.getRole() );
                }
            }
        }

        List<Role> list = new ArrayList<Role>( roles );
        Collections.sort( list, new Comparator<Role>() {
            @Override
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
        Flow flow = getFlow( id );
        if ( flow == null )
            throw new NotFoundException();
        else
            return flow;
    }

    public Flow getFlow( Long id ) {
        Flow flow = null;
        Iterator<Flow> flows = flows();
        while ( flow == null && flows.hasNext() ) {
            Flow f = flows.next();
            if ( f.getId() == id ) flow = f;
        }
        return flow;
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
            @Override
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
        sb.append( eventPhase.toString() );
        if ( !context.isEmpty() ) {
            sb.append( ", " );
        }
        Iterator<EventTiming> eventTimings = context.iterator();
        while ( eventTimings.hasNext() ) {
            sb.append( eventTimings.next() );
            if ( eventTimings.hasNext() ) {
                sb.append( " and " );
            }
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
        if ( getEvent().equals( initiator ) && getPhase().isConcurrent() )
            return "causes event \"" + getEvent().getName() + '\"';
        else {
            Segment partSegment = part.getSegment();
            return part.isTerminatesEventPhase()
                    && partSegment.getPhase().isConcurrent()
                    && getEvent().equals( partSegment.getEvent() )
                    && getPhase().isPostEvent() ?
                    "terminates event \"" + getEvent().getName().toLowerCase() + '\"'
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
        if ( equals( part.getSegment() ) && part.isTerminatesEventPhase() ) {
            return getPhase().isPreEvent()
                    ? "can prevent " + getEvent().getName()
                    : "terminates " + getPhaseEventTitle().toLowerCase();
        } else if ( getEvent().equals( initiator ) && getPhase().isPreEvent() ) {
            return "causes event \"" + initiator.getName().toLowerCase() + '\"';
        } else {
            return "";
        }
/*
        return equals( part.getSegment() ) && part.isTerminatesEventPhase()
                ? "terminates " + getPhaseEventTitle().toLowerCase()
                : getEvent().equals( initiator ) && getPhase().isPreEvent()
                    ? "causes event \"" + initiator.getName().toLowerCase() + '\"'
                    : "";
*/
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
                    @Override
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
     * At least one goal is achieved when the segment ends.
     *
     * @return a boolean
     */
    public boolean hasTerminatingGoals() {
        return !getTerminatingGoals().isEmpty();
    }


    /**
     * Get all goals achieved when the segment terminates.
     *
     * @return a list of goals
     */
    @SuppressWarnings( "unchecked" )
    public List<Goal> getTerminatingGoals() {
        return (List<Goal>)CollectionUtils.select( goals,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Goal goal = (Goal) object;
                        return goal.isEndsWithSegment();
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

    /**
     * Get all sharing flows.
     *
     * @return a list of flows
     */
    public List<Flow> getAllSharingFlows() {
        Set<Flow> flows = new HashSet<Flow>();
        Iterator<Part> parts = parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            flows.addAll( part.getAllSharingSends() );
            flows.addAll( part.getAllSharingReceives() );
        }
        return new ArrayList<Flow>( flows );
    }

    public void addToContext( EventTiming eventTiming ) {
        context.add( eventTiming );
    }

    public void initEventPhase( EventPhase eventPhase ) {
        this.eventPhase.initFrom( eventPhase );
    }

    public boolean impliesEventPhaseAndContextOf( Segment other, Place locale ) {
        return eventPhase.narrowsOrEquals( other.getEventPhase(), locale )
                && impliesContext( other.getContext(), locale );
    }

    // All event timings in other context are narrowed or equaled by an event timing in this.
    private boolean impliesContext( List<EventTiming> otherContext, final Place locale ) {
        return !CollectionUtils.exists(
                otherContext,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final EventTiming otherEventTiming = (EventTiming) object;
                        return !CollectionUtils.exists(
                                getContext(),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        EventTiming eventTiming = (EventTiming) object;
                                        return eventTiming.narrowsOrEquals( otherEventTiming, locale );
                                    }
                                }
                        );
                    }
                }
        );
    }

    public boolean sameAs( Segment other ) {
        return eventPhase.equals( other.getEventPhase() ) &&
                CollectionUtils.isEqualCollection( context, other.getContext() );
    }

    /**
     * Whether timing and event specifications (null means any) apply to the commitments event phase or context.
     *
     * @param timing     a phase timing or null
     * @param event      an event or null
     * @param planLocale a plan locale
     * @return a boolean
     */
    public boolean isInSituation( Phase.Timing timing, Event event, Place planLocale ) {
        // matches event phase
        if ( matchesEventTiming( timing,
                event,
                new EventTiming( eventPhase ),
                planLocale ) ) {
            return true;
        } else {
            // matches any of the event timings in context
            for ( EventTiming eventTiming : context ) {
                if ( matchesEventTiming( timing, event, eventTiming, planLocale ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchesEventTiming(
            Phase.Timing timing,
            Event event,
            EventTiming eventTiming,
            Place planLocale ) {
        return ( timing == null || eventTiming.getTiming() == timing )
                && ( event == null || eventTiming.getEvent().narrowsOrEquals( event, planLocale ) );
    }


    public static String classLabel() {
        return "template segments";
    }

    @Override
    public String getClassLabel() {
        return classLabel();
    }

    @Override
    public boolean isSegmentObject() {
        return false;
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
                        @Override
                        public boolean evaluate( Object object ) {
                            return !( (Flow) object ).isInternal();
                        }
                    } );
        }

        @Override
        public boolean hasNext() {
            while ( !sendIterator.hasNext() && !reqIterator.hasNext() && nodeIterator.hasNext() )
                setIterators( nodeIterator.next() );

            return sendIterator.hasNext() || reqIterator.hasNext();
        }

        @Override
        public Flow next() {
            if ( !hasNext() )
                throw new NoSuchElementException();
            return sendIterator.hasNext() ?
                    sendIterator.next() : reqIterator.next();
        }

        @Override
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

    public Phase getPhase() {
        return eventPhase.getPhase();
    }

    public void setPhase( Phase phase ) {
        eventPhase.setPhase( phase );
    }

    @Override
    public boolean references( final ModelObject mo ) {
        return eventPhase.references( mo )
                || CollectionUtils.exists(
                goals,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Goal) object ).references( mo );
                    }
                } )
                || CollectionUtils.exists(
                context,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (EventTiming) object ).references( mo );
                    }
                } );
    }
}
