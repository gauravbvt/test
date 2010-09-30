// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import com.mindalliance.channels.dao.DefinitionManager;
import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.SimpleIdGenerator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * ...
 */
public class TestResourceSpec {

    private ResourceSpec spec;

    private PlanDao planDao;

    private Actor bob;

    private Role peon;

    private Role janitor;

    private Organization walmart;

    private Place cafeteria;

    private Actor guy;

    private Organization mas;

    private Actor gal;

    private Organization hr;

    private Actor person;

    private Place building;

    private Organization company;

    @Before
    /**
     * Quick test data.
     */
    public void setUp() throws IOException {
        DefinitionManager definitionManager = new DefinitionManager(
                new File( "target/channel-test-data" ), null );
        definitionManager.delete( "test" );
        definitionManager.setIdGenerator( new SimpleIdGenerator() );
        definitionManager.getOrCreate( "test", "test", "MAS" );

        PlanManager planManager = new PlanManager( definitionManager );
        planDao = planManager.getDao( "test", true );

        // Assume others are null too
        if ( Actor.UNKNOWN == null )
            planDao.defineImmutableEntities( new ArrayList<TransmissionMedium>() );

        setUpObjects();
    }

    private void setUpObjects() {
        spec = new ResourceSpec();
        peon = new Role( "Peon" );
        planDao.add( peon );
        janitor = new Role( "janitor" );
        planDao.add( janitor );

        company = new Organization( "company" );
        company.setType();
        planDao.add( company );
        walmart = new Organization( "Walmart" );
        walmart.setActual();
        walmart.addTag( company );
        planDao.add( walmart );

        building = new Place( "building" );
        planDao.add( building );
        cafeteria = new Place( "cafeteria" );
        cafeteria.setWithin( building );
        planDao.add( cafeteria );

        person = new Actor( "person" );
        person.setType();
        planDao.add( person );
        guy = new Actor( "guy" );
        guy.setType();
        guy.addTag( person );
        planDao.add( guy );
        gal = new Actor( "gal" );
        gal.setType();
        gal.addTag( person );
        planDao.add( gal );



        bob = new Actor( "Bob" );
        bob.addTag( guy );
        planDao.add( bob );

        mas = new Organization( "MAS" );
        mas.setActual();
        mas.addTag( company );
        planDao.add( mas );
        hr = new Organization( "Human Resources" );
        hr.setActual();
        hr.setParent( mas );
        planDao.add( hr );
    }

    @Test
    public void testConstructor1() {
        Employment employment = new Employment( bob );
        Job job = new Job();
        Role role = new Role();
        job.setRole( role );
        employment.setJob( job );

        Specable r1 = new ResourceSpec( new Assignment( employment, new Part() ) );
        assertSame( bob, r1.getActor() );
        assertSame( role, r1.getRole() );
    }

    @Test
    public void testEquals() {
        assertFalse( spec.equals( "bla" ) );
        assertFalse( spec.equals( null ) );
    }

    @Test
    public void testActorName() {
        assertEquals( "(unknown contact)", spec.getActorName() );
    }

    @Test
    public void testIsness() {
        assertFalse( spec.isActor() );
        assertFalse( spec.isRole() );

        spec = new ResourceSpec( peon );
        assertFalse( spec.isActor() );
        assertTrue( spec.isRole() );

        spec = new ResourceSpec( bob, peon, null, null );
        assertTrue( spec.isActor() );
        assertFalse( spec.isRole() );
    }

    @Test
    public void testToString() {
        assertEquals( "someone", spec.toString() );

        spec = new ResourceSpec( bob );
        assertEquals( "Bob", spec.toString() );

        spec = new ResourceSpec( bob, janitor, null, null );
        assertEquals( "Bob as janitor", spec.toString() );

        ResourceSpec spec1 = new ResourceSpec( null, janitor, null, null );
        assertEquals( "any janitor", spec1.toString() );

        spec = new ResourceSpec( bob, janitor, walmart, null );
        assertEquals( "Bob as janitor from Walmart", spec.toString() );

        spec = new ResourceSpec( bob, janitor, walmart, cafeteria );
        assertEquals( "Bob as janitor from Walmart for cafeteria", spec.toString() );

        spec = new ResourceSpec( null, janitor, walmart, cafeteria );
        assertEquals( "janitor, Walmart, cafeteria", spec.displayString( 1000 ) );
    }

    @Test
    public void testReportTitle() {
        assertEquals( "Someone", spec.getReportTitle() );
        assertEquals( "", spec.getDescription() );

        Organization org = new Organization( "Nowhere" );
        org.setDescription( "bla" );

        spec = new ResourceSpec( org );
        assertEquals( "Nowhere", spec.getReportTitle() );
        assertEquals( "bla", spec.getDescription() );

        Role role = new Role( "plumber" );
        role.setDescription( "bla2" );

        spec = new ResourceSpec( role );
        assertEquals( "plumber", spec.getReportTitle() );
        assertEquals( "bla2", spec.getDescription() );

        bob.setDescription( "bla3" );

        spec = new ResourceSpec( bob );
        assertEquals( "Bob", spec.getReportTitle() );
        assertEquals( "bla3", spec.getDescription() );
    }

    @Test
    public void testHasEntity() {
        assertTrue( spec.hasEntity( Actor.UNKNOWN ) );
        assertTrue( spec.hasEntity( Organization.UNKNOWN ) );
        assertTrue( spec.hasEntity( Place.UNKNOWN ) );
        assertTrue( spec.hasEntity( Role.UNKNOWN ) );

        assertFalse( spec.hasEntity( bob ) );

        spec = new ResourceSpec( bob );
        assertTrue( spec.hasEntity( bob ) );
        assertFalse( spec.hasEntity( Actor.UNKNOWN ) );
        assertTrue( spec.hasEntity( guy ) );
        assertFalse( spec.hasEntity( gal ) );
    }

    @Test
    public void testGetJob() {
        assertNull( spec.getJob( null ) );

        spec = new ResourceSpec( hr );
        assertNull( spec.getJob( null ) );

        Role role = new Role( "copyboy" );
        role.setId( 4 );
        Job job = new Job( bob, role, null );
        hr.addJob( job );
        assertNull( spec.getJob( null ) );

        spec = new ResourceSpec( bob, role, hr, null );
        assertEquals( job, spec.getJob( null ) );
    }

    @Test
    public void testHasEntityOrBroader() {
        assertFalse( spec.hasEntityOrBroader( bob, null ) );
        assertTrue( new ResourceSpec( bob ).hasEntityOrBroader( bob, null ) );

        assertFalse( spec.hasEntityOrBroader( janitor, null ) );
        assertTrue( new ResourceSpec( janitor ).hasEntityOrBroader( janitor, null ) );

        assertFalse( spec.hasEntityOrBroader( walmart, null ) );
        assertTrue( new ResourceSpec( walmart ).hasEntityOrBroader( walmart, null ) );

        assertFalse( spec.hasEntityOrBroader( cafeteria, null ) );
        assertTrue( new ResourceSpec( cafeteria ).hasEntityOrBroader( cafeteria, null ) );
    }

    @Test
    public void testMatches() {
        spec = new ResourceSpec( mas );
        assertFalse( spec.matchesOrSubsumedBy( walmart, true, null ) );
    }

    @Test
    public void sanityTest() {
        spec = new ResourceSpec( bob, janitor, hr, cafeteria );

        assertTrue( spec.narrowsOrEquals( spec, null ) );
        assertTrue( spec.narrowsOrEquals( bob, null ) );
        assertTrue( spec.narrowsOrEquals( guy, null ) );
        assertTrue( spec.narrowsOrEquals( person, null ) );
        assertTrue( spec.narrowsOrEquals( janitor, null ) );
        assertTrue( spec.narrowsOrEquals( hr, null ) );
        assertTrue( spec.narrowsOrEquals( mas, null ) );
        assertTrue( spec.narrowsOrEquals( company, null ) );
        assertTrue( spec.narrowsOrEquals( cafeteria, null ) );
        assertTrue( spec.narrowsOrEquals( building, null ) );

        assertFalse( spec.narrowsOrEquals( Actor.UNKNOWN, null ) );
        assertFalse( spec.narrowsOrEquals( Role.UNKNOWN, null ) );
        assertFalse( spec.narrowsOrEquals( Organization.UNKNOWN, null ) );
        assertFalse( spec.narrowsOrEquals( Place.UNKNOWN, null ) );

        assertFalse( spec.narrowsOrEquals( walmart, null ) );
        assertFalse( spec.narrowsOrEquals( peon, null ) );
        assertFalse( spec.narrowsOrEquals( gal, null ) );
        assertFalse( spec.narrowsOrEquals( new ResourceSpec( bob, null, walmart, null ), null ) );

    }

}
