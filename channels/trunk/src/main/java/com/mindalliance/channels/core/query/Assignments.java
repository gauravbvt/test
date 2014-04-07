// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.core.query;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Query facade on a collection of assignments.
 */
public class Assignments implements Iterable<Assignment>, Serializable {

    private final Place locale;

    private final Map<Segment, Set<Assignment>> segmentMap = new HashMap<Segment, Set<Assignment>>();

    //--------------------------------------
    public Assignments( Place locale ) {
        this.locale = locale;
    }

    @Override
    public String toString() {
        return "Assignments(" + size() + ')';
    }

    public Place getLocale() {
        return locale;
    }

    void add( Collection<Assignment> assignments ) {
        for ( Assignment a : assignments )
            add( a );
    }

    public void add( Assignments assignments ) {
        for ( Assignment a : assignments )
            add( a );
    }

    public void add( Assignment assignment ) {
        Segment segment = assignment.getPart().getSegment();
        Set<Assignment> as = segmentMap.get( segment );
        if ( as == null ) {
            as = new HashSet<Assignment>();
            segmentMap.put( segment, as );
        }
        as.add( assignment );
    }

    //--------------------------------------
    public Assignments withAll( Collection<? extends Specable> specs ) {
        if ( specs == null ) return this;
        Assignments result = new Assignments( locale );

        for ( Assignment assignment : this ) {
            boolean match = false;
            ResourceSpec spec = new ResourceSpec( assignment );
            for ( Iterator<? extends Specable> it = specs.iterator(); it.hasNext() && !match; )
                if ( !spec.narrowsOrEquals( it.next(), locale ) )
                    match = true;

            if ( !match )
                result.add( assignment );
        }

        return result;
    }

    public Assignments with( Collection<? extends Specable> parms ) {
        if ( parms == null ) return this;
        Assignments result = new Assignments( locale );

        for ( Assignment assignment : this ) {
            ResourceSpec assignmentSpec = new ResourceSpec( assignment );
            boolean match = false;
            for ( Iterator<? extends Specable> it = parms.iterator(); it.hasNext() && !match; )
                if ( assignmentSpec.narrowsOrEquals( it.next(), locale ) )
                    match = true;

            if ( match )
                result.add( assignment );
        }

        return result;
    }

    public Assignments without( Collection<? extends Specable> parms ) {
        if ( parms == null ) return this;
        Assignments result = new Assignments( locale );

        for ( Assignment assignment : this ) {
            ResourceSpec assignmentSpec = new ResourceSpec( assignment );
            boolean matchedOne = false;
            for ( Iterator<? extends Specable> it = parms.iterator(); it.hasNext() && !matchedOne; )
                if ( assignmentSpec.narrowsOrEquals( it.next(), locale ) )
                    matchedOne = true;

            if ( !matchedOne )
                result.add( assignment );
        }

        return result;
    }

    public Assignments withAll( Specable... specs ) {
        if ( specs[0] == null ) return this;
        return withAll( Arrays.asList( specs ) );
    }

    public Assignments with( Specable... specs ) {
        if ( specs[0] == null ) return this;
        return with( Arrays.asList( specs ) );
    }

    public Assignments without( Specable... specs ) {
        if ( specs[0] == null ) return this;
        return without( Arrays.asList( specs ) );
    }

    public Assignments with( Segment... segments ) {
        if ( segments == null ) return this;
        Assignments result = new Assignments( locale );

        for ( Segment segment : segments ) {
            Set<Assignment> assignments = segmentMap.get( segment );
            if ( assignments == null )
                throw new IllegalArgumentException( "Unknown segment" );
            result.add( assignments );
        }
        return result;
    }

    public Assignments with( Event... events ) {
        if ( events == null ) return this;
        Set<Event> eventSet = new HashSet<Event>( Arrays.asList( events ) );
        Assignments result = new Assignments( locale );

        for ( Segment segment : segmentMap.keySet() )
            if ( eventSet.contains( segment.getEvent() ) )
                result.add( segmentMap.get( segment ) );

        return result;
    }

    public Assignments with( Phase... phases ) {
        if ( phases == null ) return this;
        Set<Phase> phaseSet = new HashSet<Phase>( Arrays.asList( phases ) );
        Assignments result = new Assignments( locale );

        for ( Segment segment : segmentMap.keySet() )
            if ( phaseSet.contains( segment.getPhase() ) )
                result.add( segmentMap.get( segment ) );

        return result;
    }

    public Assignments with( EventPhase... eventPhases ) {
        if ( eventPhases == null ) return this;
        Set<EventPhase> eventPhaseSet = new HashSet<EventPhase>( Arrays.asList( eventPhases ) );
        Assignments result = new Assignments( locale );

        for ( Segment segment : segmentMap.keySet() )
            if ( eventPhaseSet.contains( segment.getEventPhase() ) )
                result.add( segmentMap.get( segment ) );

        return result;
    }

    public Assignments with( Node node ) {
        if ( node == null ) return this;
        if ( node.isPart() )
            return with( (Specable) node );

        Assignments result = new Assignments( locale );
        for ( ExternalFlow externalFlow : ( (Connector) node ).getExternalFlows() )
            result.add( with( externalFlow.getPart() ) );
        return result;
    }

    //--------------------------------------
    public List<Segment> getSegments() {
        return toSortedList( segmentMap.keySet() );
    }

    public List<Organization> getOrganizations() {
        Map<Organization, Integer> orgCounts = new HashMap<Organization, Integer>();

        for ( Assignment a : this ) {
            Organization organization = a.getOrganization();
            do {
                Integer count = orgCounts.get( organization );
                orgCounts.put( organization, count == null ? 1 : count + 1 );
                organization = organization.getParent();
            } while ( organization != null );
        }

        // Ignore parents with a child with equal counts
        List<Organization> result = new ArrayList<Organization>( orgCounts.size() );

        Set<Organization> set = orgCounts.keySet();
        for ( Organization o : set ) {
            Integer oco = orgCounts.get( o );
            boolean redundant = false;
            for ( Iterator<Organization> it = set.iterator(); it.hasNext() && !redundant; ) {
                Organization o2 = it.next();
                if ( o2.isWithin( o, locale ) && oco.equals( orgCounts.get( o2 ) ) )
                    redundant = true;
            }

            if ( !redundant )
                result.add( o );
        }

        Collections.sort( result );
        return result;
    }

    public List<Actor> getActualActors() {
        Set<Actor> actors = new HashSet<Actor>();
        for ( Assignment a : this ) {
            Actor actor = a.getActor();
            if ( actor != null ) actors.add( actor );
        }

        List<Actor> result = new ArrayList<Actor>( actors );
        Collections.sort( result, new Comparator<Actor>() {
            public int compare( Actor o1, Actor o2 ) {
                return o1.getNormalizedName().compareToIgnoreCase( o2.getNormalizedName() );
            }
        } );
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Actor> getActualKnownActors() {
        return (List<Actor>) CollectionUtils.select(
                getActualActors(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (Actor) object ).isUnknown();
                    }
                }
        );
    }


    public List<Role> getRoles() {
        Set<Role> roles = new HashSet<Role>();
        for ( Assignment a : this ) {
            Role role = a.getRole();
            if ( role != null ) roles.add( role );
        }

        return toSortedList( roles );
    }

    public List<Specable> getActors() {
        Set<Specable> specables = new HashSet<Specable>();
        for ( Assignment assignment : this )
            specables.add( assignment.getSpecableActor() );

        List<Specable> result = new ArrayList<Specable>( specables );
        Collections.sort( result, new Comparator<Specable>() {
            public int compare( Specable o1, Specable o2 ) {
                return stringify( o1 ).compareToIgnoreCase( stringify( o2 ) );
            }
        } );
        return result;
    }

    public List<Flow> getSharingFlows() {
        Set<Flow> flows = new HashSet<Flow>();
        for ( Assignment assignment : this ) {
            Part part = assignment.getPart();
            flows.addAll( part.getAllSharingSends() );
            flows.addAll( part.getAllSharingReceives() );
        }
        return new ArrayList<Flow>( flows );
    }

    public static String stringify( Specable specable ) {
        return specable instanceof Actor ? ( (Actor) specable ).getNormalizedName()
                : ( (Role) specable ).reportString();
    }

    private static <T extends Comparable> List<T> toSortedList( Collection<T> collection ) {
        List<T> result = new ArrayList<T>( collection );
        Collections.sort( result );
        return result;
    }

    public List<Place> getJurisdictions() {
        Set<Place> jurisdictions = new HashSet<Place>();
        for ( Assignment a : this ) {
            Place place = a.getJurisdiction();
            jurisdictions.add( place == null ? Place.UNKNOWN : place );
        }

        return toSortedList( jurisdictions );
    }

    public List<Place> getLocations() {
        Set<Place> locations = new HashSet<Place>();
        for ( Assignment a : this ) {
            Place place = a.getLocation();
            locations.add( place == null ? Place.UNKNOWN : place );
        }

        return toSortedList( locations );
    }

    public List<Event> getEvents() {
        Set<Event> events = new HashSet<Event>( segmentMap.size() );
        for ( Segment segment : segmentMap.keySet() ) {
            Event event = segment.getEvent();
            if ( event != null )
                events.add( event );
        }

        return toSortedList( events );
    }

    public List<Phase> getPhases() {
        Set<Phase> phases = new HashSet<Phase>( segmentMap.size() );
        for ( Segment segment : segmentMap.keySet() ) {
            Phase phase = segment.getPhase();
            if ( phase != null )
                phases.add( phase );
        }

        return toSortedList( phases );
    }

    public List<EventPhase> getEventPhases() {
        Set<EventPhase> eventPhases = new HashSet<EventPhase>( segmentMap.size() );
        for ( Segment segment : segmentMap.keySet() ) {
            EventPhase eventPhase = segment.getEventPhase();
            if ( eventPhase != null )
                eventPhases.add( eventPhase );
        }

        return toSortedList( eventPhases );
    }

    //--------------------------------------
    public List<Part> getParts() {
        Set<Part> parts = new HashSet<Part>();
        for ( Assignment assignment : this )
            parts.add( assignment.getPart() );

        return toSortedList( parts );
    }

    /**
     * Find assignments that are started with the segments.
     *
     * @param queryService a query service
     * @return a list of assignments
     */
    public Assignments getImmediates( QueryService queryService ) {
        Assignments result = new Assignments( locale );

        for ( Assignment assignment : this )
            if ( isImmediate( assignment.getPart(), queryService ) )
                result.add( assignment );

        return result;
    }

    public static boolean isImmediate( Part part, QueryService queryService ) {
        return part.isStartsWithSegment();
    }

    /**
     * Find assignments associated with task with no specific start time (bug in the model).
     *
     * @param queryService a query service
     * @return a list of assignments
     */
    public Assignments getOptionals( QueryService queryService ) {
        Assignments result = new Assignments( locale );

        for ( Assignment assignment : this )
            if ( isOptional( assignment.getPart(), queryService ) )
                result.add( assignment );

        return result;
    }

    public static boolean isOptional( Part part, QueryService queryService ) {
        return !isImmediate( part, queryService ) && !isNotification( part, queryService ) && !isRequest( part );
    }

    /**
     * Find assignments triggered by an incoming notification.
     *
     * @param queryService a query service
     * @return a list of assignments
     */
    public Assignments getNotifications( QueryService queryService ) {
        Assignments result = new Assignments( locale );

        for ( Assignment assignment : this )
            if ( isNotification( assignment.getPart(), queryService ) )
                result.add( assignment );

        return result;
    }

    public static boolean isNotification( Part part, QueryService queryService ) {
        boolean found = false;
        Iterator<Flow> flows = part.flows();
        while ( flows.hasNext() && !found ) {
            Flow flow = flows.next();
            found = part.equals( flow.getTarget() )
                    && flow.isTriggeringToTarget()
                    && !flow.isAskedFor()
                    && !flow.isProhibited();
        }
        return found;
    }

    /**
     * Find assignments triggered by a request for information.
     *
     * @return a list of parts
     */
    public Assignments getRequests() {
        Assignments result = new Assignments( locale );

        for ( Assignment assignment : this )
            if ( isRequest( assignment.getPart() ) )
                result.add( assignment );

        return result;
    }

    public static boolean isRequest( Part part ) {
        boolean found = false;
        Iterator<Flow> flows = part.flows();
        while ( flows.hasNext() && !found ) {
            Flow flow = flows.next();
            found = part.equals( flow.getSource() )
                    && flow.isTriggeringToSource()
                    && flow.isAskedFor();
        }
        return found;
    }

    public Assignments forSegment( Segment segment ) {
        if ( segment == null ) return this;
        Assignments result = new Assignments( locale );
        if ( segmentMap.containsKey( segment ) )
            for ( Assignment assignment : segmentMap.get( segment ) )
                result.add( assignment );
        return result;
    }


    public Assignments assignedTo( Part part ) {
        if ( part == null ) return this;
        Assignments result = new Assignments( locale );
        if ( segmentMap.containsKey( part.getSegment() ) ) {
            for ( Assignment assignment : segmentMap.get( part.getSegment() ) )
                if ( part.equals( assignment.getPart() ) )
                    result.add( assignment );
        }
        return result;
    }

    public Assignments getSources( Part part ) {
        if ( part == null ) return this;
        Assignments sources = new Assignments( locale );
        for ( Iterator<Flow> flows = part.flows(); flows.hasNext(); ) {
            Flow flow = flows.next();
            Node node =
                    part.equals( flow.getTarget() ) && flow.isTriggeringToTarget() ? flow.getSource()
                            : part.equals( flow.getSource() ) && flow.isTriggeringToSource() ? flow.getTarget()
                            : null;

            if ( node != null ) {
                if ( node.isPart() )
                    sources.add( assignedTo( (Part) node ) );
                else {
                    for ( ExternalFlow externalFlow : ( (Connector) node ).getExternalFlows() )
                        sources.add( assignedTo( externalFlow.getPart() ) );
                }
            }
        }
        return sources;
    }

    public Assignments notFrom( Specable source ) {
        if ( source == null ) return this;
        Assignments result = new Assignments( locale );
        for ( Assignment assignment : this ) {
            boolean found = false;
            Assignments sources = getSources( assignment.getPart() );
            for ( Iterator<Assignment> it = sources.iterator(); it.hasNext() && !found; )
                if ( new ResourceSpec( it.next() ).narrowsOrEquals( source, locale ) )
                    found = true;

            if ( !found )
                result.add( assignment );
        }
        return result;
    }

    public Assignments from( Specable source ) {
        if ( source == null ) return this;
        Assignments result = new Assignments( locale );
        for ( Assignment other : this ) {
            boolean found = false;
            Assignments sources = getSources( other.getPart() );
            for ( Iterator<Assignment> it = sources.iterator(); it.hasNext() && !found; )
                if ( new ResourceSpec( it.next() ).narrowsOrEquals( source, locale ) )
                    found = true;

            if ( !sources.isEmpty() && found )
                result.add( other );
        }
        return result;
    }

    public Assignments from( Assignment source ) {
        if ( source == null ) return this;
        Assignments result = new Assignments( locale );
        for ( Assignment other : this )
            if ( getSources( other.getPart() ).contains( source ) )
                result.add( other );
        return result;
    }

    public Assignments producesAssets() {
        Assignments result = new Assignments( locale );
        for ( Assignment other : this )
            if ( !other.getPart().getAssetConnections().producing().isEmpty() )
                result.add( other );
        return result;
    }

    public Assignments producesAsset( MaterialAsset asset ) {
        Assignments result = new Assignments( locale );
        for ( Assignment other : this )
            if ( !other.getPart().getAssetConnections().producing().about( asset ).isEmpty() )
                result.add( other );
        return result;
    }


    public Assignments usesAssets() {
        Assignments result = new Assignments( locale );
        for ( Assignment other : this )
            if ( !other.getPart().findAssetsUsed().isEmpty() )
                result.add( other );
        return result;
    }

    public Assignments onlyUsesAssets() { // does not produce
        Assignments result = new Assignments( locale );
        for ( Assignment other : this )
            if ( !other.getPart().findAssetsUsed().isEmpty()
                    && other.getPart().getAssetConnections().producing().isEmpty() )
                result.add( other );
        return result;
    }

    public Assignments onlyUsesAsset( final MaterialAsset asset ) { // does not produce
        Assignments result = new Assignments( locale );
        for ( Assignment other : this ) {
            boolean assetUsed = CollectionUtils.exists(
                    other.getPart().findAssetsUsed(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (MaterialAsset) object ).narrowsOrEquals( asset );
                        }
                    }
            );
            if ( assetUsed
                    && other.getPart().getAssetConnections().producing().about( asset ).isEmpty() )
                result.add( other );
        }
        return result;
    }


    //--------------------------------------

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @SuppressWarnings( {"unchecked"} )
    public Iterator<Assignment> iterator() {
        Iterator<Assignment>[] iterators = new Iterator[segmentMap.size()];
        int i = 0;
        for ( Segment s : getSegments() )
            iterators[i++] = segmentMap.get( s ).iterator();

        return (Iterator<Assignment>) IteratorUtils.chainedIterator( iterators );
    }

    public boolean isEmpty() {
        return segmentMap.isEmpty();
    }

    public int size() {
        int result = 0;
        for ( Set<Assignment> assignments : segmentMap.values() )
            result += assignments.size();
        return result;
    }

    public boolean contains( Assignment assignment ) {
        for ( Set<Assignment> assignments : segmentMap.values() )
            if ( assignments.contains( assignment ) )
                return true;
        return false;
    }

    public List<Assignment> getAssignments() {
        List<Assignment> result = new ArrayList<Assignment>();
        for ( Segment s : getSegments() )
            result.addAll( segmentMap.get( s ) );
        return result;
    }

    /**
     * Find a description for all assignments.
     *
     * @param basis a common known spec
     * @return a description spec
     */
    public ResourceSpec getCommonSpec( Specable basis ) {
        if ( isEmpty() )
            return new ResourceSpec();

        ResourceSpec spec = null;

        for ( Assignment source : this ) {
            if ( spec == null )
                spec = new ResourceSpec( source );

            else if ( !spec.narrowsOrEquals( source, locale ) )
                spec = new ResourceSpec(
                        getCommon( spec.getActor(), source.getActor(),
                                basis == null ? null : basis.getActor() ),
                        getCommon( spec.getRole(), source.getRole(),
                                basis == null ? null : basis.getRole() ),
                        getCommon( spec.getOrganization(), source.getOrganization(),
                                basis == null ? null : basis.getOrganization() ),
                        getCommon( spec.getJurisdiction(), source.getJurisdiction(), null )
                );
        }

        return spec;
    }

    private <E extends ModelEntity> E getCommon( E common, E actor, E basis ) {
        return common != null && common.narrowsOrEquals( actor, locale ) ? common : basis;
    }

}
