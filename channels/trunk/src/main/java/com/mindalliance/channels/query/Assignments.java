// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.query;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.model.Job;
import org.apache.commons.collections.IteratorUtils;

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
public class Assignments implements Iterable<Assignment> {

    private Place locale;

    private Map<Segment, Set<Assignment>> segmentMap = new HashMap<Segment,Set<Assignment>>();

    //--------------------------------------
    private Assignments( Place locale ) {
        this.locale = locale;
    }

    public static Assignments getAssignments( QueryService service ) {
        Assignments result = new Assignments( service.getPlan().getLocale() );

        for ( Segment segment : service.list( Segment.class ) )
            for ( Iterator<Part> pi = segment.parts(); pi.hasNext(); )
                result.add( segment, findAssignments( service, pi.next() ) );

        return result;
    }

    private void add( Segment segment, Collection<Assignment> assignments ) {
        Set<Assignment> as = segmentMap.get( segment );
        if ( as == null ) {
            as = new HashSet<Assignment>();
            segmentMap.put( segment, as );
        }
        as.addAll( assignments );
    }

    private static List<Assignment> findAssignments( QueryService service, Part part ) {
        Place locale = service.getPlan().getLocale();
        List<Assignment> result = new ArrayList<Assignment>();

        for ( Employment e : findActorEmployments( service, part, locale ) )
            if (    ModelEntity.implies( e.getActor(),        part.getActor(), locale )
                 && ModelEntity.implies( e.getRole(),         part.getRole(), locale )
                 && ModelEntity.implies( e.getOrganization(), part.getOrganization(), locale )
                 && ModelEntity.implies( e.getJurisdiction(), part.getJurisdiction(), locale ) )
                result.add( new Assignment( e, part ) );

        // No actor for this part. Add an unknown one.
        if ( result.isEmpty() && !part.resourceSpec().isAnyone()
                              && part.getActorOrUnknown().isUnknown() )

            result.add( new Assignment(
                            new Employment( Actor.UNKNOWN,
                                            part.getOrganizationOrUnknown(),
                                            new Job( Actor.UNKNOWN,
                                                     part.getRoleOrUnknown(),
                                                     part.getJurisdiction() ) ),
                            part ) );

        return result;
    }

    private static List<Employment> findActorEmployments(
            QueryService service, Part part, Place locale ) {

        Set<Actor> employed = new HashSet<Actor>();
        List<Employment> employments = new ArrayList<Employment>();

        for ( Organization org : service.listActualEntities( Organization.class ) ) {
            List<Job> confirmedJobs = org.getJobs();

            for ( Job job : confirmedJobs ) {
                employments.add( new Employment( job.getActor(), org, job ) );
                employed.add( job.getActor() );
            }

            if ( org.narrowsOrEquals( part.getOrganizationOrUnknown(), locale )
                 && part.hasActualActor() && part.getOrganization() != null ) {

                Actor actor = part.getActor();
                Job j = new Job( actor, part.getRole(), part.getJurisdiction() );
                if ( !confirmedJobs.contains( j )
                     && ( !actor.isArchetype() || !employed.contains( actor ) ) ) {
                    employments.add( new Employment( actor, org, j ) );
                    employed.add( actor );
                }
            }
        }

        for ( Actor actor : service.listActualEntities( Actor.class ) )
            if ( !employed.contains( actor ) )
                employments.add( new Employment( actor ) );

        return employments;
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

    public int size() {
        int result = 0;
        for ( Set<Assignment> assignments : segmentMap.values() )
            result += assignments.size();
        return result;
    }
}
