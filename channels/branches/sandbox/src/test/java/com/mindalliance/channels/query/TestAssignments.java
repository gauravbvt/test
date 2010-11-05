// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.query;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.Specable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.TestExecutionListeners;

import java.util.List;

/**
 * ...
 */
@TestExecutionListeners( AbstractChannelsTest.InstallSamplesListener.class )
public class TestAssignments extends AbstractChannelsTest {

    private Assignments assignments;

    public TestAssignments() {
        super( "denis", "mindalliance.com/channels/plans/railsec" );
    }

    @Before
    public void init() {
        assignments = queryService.getAssignments();
    }

    @Test
    public void testSize() {
        assertEquals( 44, assignments.size() );
    }

    @Test
    public void testGetSegments() {
        List<Segment> segments = assignments.getSegments();
        assertEquals( 3, segments.size() );
        assertEquals( "IED attack suspected", segments.get( 0 ).getName() );
        assertEquals( "IED threat warning", segments.get( 1 ).getName() );
        assertEquals( "Local response plan activated", segments.get( 2 ).getName() );
    }

    @Test
    public void testGetActors() {
        List<Specable> actors = assignments.getActors();
        assertEquals( 20, actors.size() );
    }

    @Test
    public void testGetActualActors() {
        List<Actor> actualActors = assignments.getActualActors();
        assertEquals( 17, actualActors.size() );
        assertSame( Actor.UNKNOWN, actualActors.get( 0 ) );
        assertEquals( "A bus driver", actualActors.get( 1 ).getName() );
        assertEquals( "Claire Waters", actualActors.get( actualActors.size() - 1 ).getName() );
    }

    @Test
    public void testGetOrganizations() {
        List<Organization> orgs = assignments.getOrganizations();
        assertEquals( 8, orgs.size() );
    }

    @Test
    public void testGetOrganizations2() {
        Organization bogus = new Organization( "Bogus" );
        bogus.setActual();
        queryService.add( bogus );
        Organization mta =
                queryService.findOrCreate( Organization.class, "Metropolis Transit Authority" );
        mta.setParent( bogus );

        assignments = queryService.getAssignments();
        List<Organization> orgs = assignments.getOrganizations();
        assertEquals( 8, orgs.size() );
    }

    @Test
    public void testGetLocations() {
        List<Place> places = assignments.getLocations();
        assertEquals( 4, places.size() );
    }

    @Test
    public void testGetJurisdictions() {
        List<Place> places = assignments.getJurisdictions();
        assertEquals( 6, places.size() );
    }

    @Test
    public void testGetRoles() {
        List<Role> roles = assignments.getRoles();
        assertEquals( 16, roles.size() );
    }

    @Test
    public void testWithSegment() {
        List<Segment> segments = assignments.getSegments();
        Assignments a = assignments.withSome( segments.get( 0 ), segments.get( 1 ) );
        assertEquals( 34, a.size() );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testWithSegment2() {
        assignments.withSome( new Segment() );
    }

    @Test
    public void testWithAll() {
        List<Organization> orgs = assignments.getOrganizations();

        Organization organization = orgs.get( 4 );
        Assignments a1 = assignments.withAll( organization );
        assertEquals( 39, a1.size() );

        List<Role> roles = a1.getRoles();
        assertEquals( 12, roles.size() );

        Assignments a2 = assignments.withAll( organization, roles.get( 6 ) );
        List<Specable> actors = a2.getActors();
        assertEquals( 10, a2.size() );
        assertEquals( 2, actors.size() );
    }

    @Test
    public void testWithSome() {
        int size = assignments.size();
        assertEquals( size, assignments.withSome( assignments.getOrganizations() ).size() );

        assertEquals( size, assignments.withSome( assignments.getRoles() ).size() );
        int total = 0;
        for ( Role role : assignments.getRoles() )
            total += assignments.withSome( role ).size();
        assertEquals( size, total );

        assertEquals( size, assignments.withSome( assignments.getActors() ).size() );
        total = 0;
        for ( Specable actor : assignments.getActors() )
            total += assignments.withSome( actor ).size();
        assertEquals( size, total );

        assertEquals( size, assignments.withSome( assignments.getActualActors() ).size() );
        total = 0;
        for ( Actor actor : assignments.getActualActors() )
            total += assignments.withSome( actor ).size();
        assertEquals( size, total );

        assertEquals( size, assignments.withSome( assignments.getJurisdictions() ).size() );
        total = 0;
        for ( Specable place : assignments.getJurisdictions() )
            total += assignments.withSome( place ).size();
        assertEquals( size, total );

        total = 0;
        for ( Event event : assignments.getEvents() )
            total += assignments.withSome( event ).size();
        assertEquals( size, total );

        total = 0;
        for ( Phase phase : assignments.getPhases() )
            total += assignments.withSome( phase ).size();
        assertEquals( size, total );
    }

    @Test
    public void testWithSome2() {
        Assignments a = assignments.withSome( Actor.UNKNOWN );
        assertEquals( 4, a.size() );
    }
}
