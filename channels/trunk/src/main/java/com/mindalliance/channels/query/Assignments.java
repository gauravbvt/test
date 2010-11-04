// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.query;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.Specable;
import org.apache.commons.collections.IteratorUtils;

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

    private Place locale;

    private Map<Segment, Set<Assignment>> segmentMap = new HashMap<Segment,Set<Assignment>>();

    //--------------------------------------
    Assignments( Place locale ) {
        this.locale = locale;
    }

    void add( Segment segment, Collection<Assignment> assignments ) {
        Set<Assignment> as = segmentMap.get( segment );
        if ( as == null ) {
            as = new HashSet<Assignment>();
            segmentMap.put( segment, as );
        }
        as.addAll( assignments );
    }

    //--------------------------------------
    public Assignments withAll( Collection<? extends Specable> specs ) {
        Assignments result = new Assignments( locale );

        for ( Segment segment : getSegments() ) {
            Collection<Assignment> initial = segmentMap.get( segment );
            List<Assignment> selection = new ArrayList<Assignment>( initial.size() );
            for ( Assignment assignment : initial ) {
                boolean matchedAll = true;
                ResourceSpec spec = new ResourceSpec( assignment );
                for ( Iterator<? extends Specable> it = specs.iterator();
                      it.hasNext() && matchedAll; )

                    if ( !spec.narrowsOrEquals( it.next(), locale ) )
                            matchedAll = false;

                if ( matchedAll )
                    selection.add( assignment );
            }

            if ( !selection.isEmpty() )
                result.add( segment, selection );
        }

        return result;
    }

    public Assignments withSome( Collection<? extends Specable> parms ) {
        Assignments result = new Assignments( locale );

        for ( Segment segment : getSegments() ) {
            Collection<Assignment> initial = segmentMap.get( segment );
            List<Assignment> selection = new ArrayList<Assignment>( initial.size() );
            for ( Assignment assignment : initial ) {
                ResourceSpec assignmentSpec = new ResourceSpec( assignment );
                boolean matchedOne = false;
                for ( Iterator<? extends Specable> it = parms.iterator();
                      it.hasNext() && !matchedOne; ) {
                    Specable parm = it.next();
                    if ( assignmentSpec.narrowsOrEquals( parm, locale ) )
                            matchedOne = true;
                }

                if ( matchedOne )
                    selection.add( assignment );
            }

            if ( !selection.isEmpty() )
                result.add( segment, selection );
        }

        return result;
    }

    public Assignments withAll( Specable... specs ) {
        return withAll( Arrays.asList( specs ) );
    }

    public Assignments withSome( Specable... specs ) {
        return withSome( Arrays.asList( specs ) );
    }

    public Assignments withSome( Segment... segments ) {
        Assignments result = new Assignments( locale );

        for ( Segment segment : segments ) {
            Set<Assignment> assignments = segmentMap.get( segment );
            if ( assignments == null )
                throw new IllegalArgumentException( "Unknown segment" );
            result.add( segment, assignments );
        }
        return result;
    }

    public Assignments withSome( Event... events ) {
        Set<Event> eventSet = new HashSet<Event>( Arrays.asList( events ) );
        Assignments result = new Assignments( locale );

        for ( Segment segment : segmentMap.keySet() )
            if ( eventSet.contains( segment.getEvent() ) )
                result.add( segment, segmentMap.get( segment ) );

        return result;
    }

    public Assignments withSome( Phase... phases ) {
        Set<Phase> phaseSet = new HashSet<Phase>( Arrays.asList( phases ) );
        Assignments result = new Assignments( locale );

        for ( Segment segment : segmentMap.keySet() )
            if ( phaseSet.contains( segment.getPhase() ) )
                result.add( segment, segmentMap.get( segment ) );

        return result;
    }

    //--------------------------------------
    public List<Segment> getSegments() {
        List<Segment> result = new ArrayList<Segment>( segmentMap.keySet() );
        Collections.sort( result );
        return result;
    }

    public List<Organization> getOrganizations() {
        Map<Organization,Integer> orgCounts = new HashMap<Organization,Integer>();

        Iterator<Assignment> assignments = iterator();
        while ( assignments.hasNext() ) {
            Organization organization = assignments.next().getOrganization();
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
        Iterator<Assignment> assignments = iterator();
        while ( assignments.hasNext() )
            actors.add( assignments.next().getActor() );

        List<Actor> result = new ArrayList<Actor>( actors );
        Collections.sort( result, new Comparator<Actor>() {
            public int compare( Actor o1, Actor o2 ) {
                return o1.getNormalizedName().compareToIgnoreCase( o2.getNormalizedName() );
            }
        } );
        return result;
    }

    public List<Role> getRoles() {
        Set<Role> roles = new HashSet<Role>();
        Iterator<Assignment> assignments = iterator();
        while ( assignments.hasNext() )
            roles.add( assignments.next().getRole() );

        List<Role> result = new ArrayList<Role>( roles );
        Collections.sort( result );
        return result;
    }

    public List<Specable> getActors() {
        Set<Specable> specables = new HashSet<Specable>();
        Iterator<Assignment> assignments = iterator();
        while ( assignments.hasNext() ) {
            Assignment assignment = assignments.next();
            Actor actor = assignment.getActor();
            Role role = assignment.getRole();
            specables.add( !actor.isUnknown() && actor.isActual() || role == null ? actor : role );
        }

        List<Specable> result = new ArrayList<Specable>( specables );
        Collections.sort( result, new Comparator<Specable>() {
            public int compare( Specable o1, Specable o2 ) {
                return stringify( o1 ).compareToIgnoreCase( stringify( o2 ) );
            }
        } );
        return result;
    }

    public static String stringify( Specable specable ) {
        return specable instanceof Actor ? ( (Actor) specable ).getNormalizedName()
                                         : ( (Role) specable ).reportString();
    }
    public List<Place> getJurisdictions() {
        Set<Place> jurisdictions = new HashSet<Place>();
        Iterator<Assignment> assignments = iterator();
        while ( assignments.hasNext() ) {
            Place place = assignments.next().getJurisdiction();
            jurisdictions.add( place == null ? Place.UNKNOWN : place );
        }

        List<Place> result = new ArrayList<Place>( jurisdictions );
        Collections.sort( result );
        return result;
    }

    public List<Place> getLocations() {
        Set<Place> locations = new HashSet<Place>();
        Iterator<Assignment> assignments = iterator();
        while ( assignments.hasNext() ) {
            Place place = assignments.next().getLocation();
            locations.add( place == null ? Place.UNKNOWN : place );
        }

        List<Place> result = new ArrayList<Place>( locations );
        Collections.sort( result );
        return result;
    }

    public List<Event> getEvents() {
        Collection<Segment> segments = getSegments();
        Set<Event> events = new HashSet<Event>( segments.size() );
        for ( Segment segment : segments ) {
            Event event = segment.getEvent();
            if ( event != null )
                events.add( event );
        }

        List<Event> result = new ArrayList<Event>( events );
        Collections.sort( result );
        return result;
    }

    public List<Phase> getPhases() {
        Collection<Segment> segments = getSegments();
        Set<Phase> phases = new HashSet<Phase>( segments.size() );
        for ( Segment segment : segments ) {
            Phase phase = segment.getPhase();
            if ( phase != null )
                phases.add( phase );
        }

        List<Phase> result = new ArrayList<Phase>( phases );
        Collections.sort( result );
        return result;
    }

    public List<Part> getParts() {
        Set<Part> parts = new HashSet<Part>();
        for ( Assignment assignment : this )
            parts.add( assignment.getPart() );

        List<Part> result = new ArrayList<Part>( parts );
        Collections.sort( result );
        return result;
    }

    //--------------------------------------
    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @SuppressWarnings( { "unchecked" } )
    public Iterator<Assignment> iterator() {
        Iterator<Assignment>[] iterators = new Iterator[ segmentMap.size() ];
        int i = 0;
        for ( Segment s : getSegments() )
            iterators[ i++ ] = segmentMap.get( s ).iterator();

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

    public List<Assignment> getAssignments() {
        List<Assignment> result = new ArrayList<Assignment>();
        for ( Segment s : getSegments() )
            result.addAll( segmentMap.get( s ) );

        return result;
    }
}
