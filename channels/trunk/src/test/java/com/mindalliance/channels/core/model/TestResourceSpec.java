// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.dao.ModelDao;
import com.mindalliance.channels.core.dao.ModelDefinitionManager;
import com.mindalliance.channels.core.dao.ModelManagerImpl;
import com.mindalliance.channels.core.dao.SimpleIdGenerator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * ...
 */
public class TestResourceSpec {

    private ResourceSpec spec;

    private ModelDao modelDao;

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

    private Organization njCo;

    private Place nj;

    private Organization nyCo;

    private Place ny;

    @Before
    /**
     * Quick test data.
     */
    public void setUp() throws IOException {
        ModelDefinitionManager modelDefinitionManager = new ModelDefinitionManager(
                new FileSystemResource( new File( "target/channel-test-data" ) ), null );
        modelDefinitionManager.delete( "test" );
        modelDefinitionManager.setIdGenerator( new SimpleIdGenerator() );
        modelDefinitionManager.getOrCreate( "test", "test", "MAS" );

        ModelManagerImpl planManager = new ModelManagerImpl( modelDefinitionManager );
        modelDao = planManager.getDao( "test", true );

        // Assume others are null too
        if ( Actor.UNKNOWN == null ) {
            modelDao.defineImmutableEntities();
            modelDao.defineImmutableMedia( new ArrayList<TransmissionMedium>() );
        }

        setUpObjects();
    }

    private void setUpObjects() {
        spec = new ResourceSpec();
        peon = new Role( "Peon" );
        modelDao.add( peon );
        janitor = new Role( "janitor" );
        modelDao.add( janitor );

        company = new Organization( "company" );
        company.setType();
        modelDao.add( company );
        walmart = new Organization( "Walmart" );
        walmart.setActual();
        walmart.addType( company );
        modelDao.add( walmart );

        building = new Place( "building" );
        building.setType();
        modelDao.add( building );
        cafeteria = new Place( "cafeteria" );
        cafeteria.setWithin( building );
        cafeteria.setType();
        modelDao.add( cafeteria );

        person = new Actor( "person" );
        person.setType();
        modelDao.add( person );
        guy = new Actor( "guy" );
        guy.setType();
        guy.addType( person );
        modelDao.add( guy );
        gal = new Actor( "gal" );
        gal.setType();
        gal.addType( person );
        modelDao.add( gal );



        bob = new Actor( "Bob" );
        bob.addType( guy );
        modelDao.add( bob );

        nj = new Place( "New Jersey" );
        nj.setActual();
        modelDao.add( nj );

        ny = new Place( "New York" );
        ny.setActual();
        modelDao.add( ny );

        njCo = new Organization( "NJ company" );
        njCo.setType();
        modelDao.add( njCo );
        njCo.addType( company );
        njCo.setLocation( nj );

        nyCo = new Organization( "NY company" );
        nyCo.setType();
        modelDao.add( nyCo );
        nyCo.addType( company );
        nyCo.setLocation( ny );

        mas = new Organization( "MAS" );
        mas.setActual();
        mas.addType( njCo );
        mas.setLocation( nj );
        modelDao.add( mas );
        hr = new Organization( "Human Resources" );
        hr.setActual();
        hr.setParent( mas );
        modelDao.add( hr );
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
        assertFalse( spec.isOrganization() );

        spec = new ResourceSpec( bob, peon, mas, null );
        assertFalse( spec.isOrganization() );
        spec = new ResourceSpec( null, null, mas, null );
        assertTrue( spec.isOrganization() );

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
        assertEquals( "Bob as janitor at Walmart", spec.toString() );

        spec = new ResourceSpec( bob, janitor, walmart, cafeteria );
        assertEquals( "Bob as janitor at Walmart for cafeteria", spec.toString() );

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
    public void testDisplayString() {
        assertEquals( "Bob", new ResourceSpec( bob ).displayString( 123 ) );
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
    public void testMatchesOrSubsumedBy() {
        assertTrue( spec.matchesOrSubsumedBy( Actor.UNKNOWN, true, null ) );
        assertTrue( spec.matchesOrSubsumedBy( Role.UNKNOWN, true, null ) );
        assertTrue( spec.matchesOrSubsumedBy( Organization.UNKNOWN, true, null ) );
        assertTrue( spec.matchesOrSubsumedBy( Place.UNKNOWN, true, null ) );

        assertFalse( new ResourceSpec( bob ).matchesOrSubsumedBy( Actor.UNKNOWN, true, null ) );
        assertFalse( new ResourceSpec( walmart ).matchesOrSubsumedBy( mas, true, null ) );
        assertFalse( new ResourceSpec( peon ).matchesOrSubsumedBy( janitor, true, null ) );
        assertFalse( new ResourceSpec( building ).matchesOrSubsumedBy( Place.UNKNOWN, true, null ) );

        assertTrue( new ResourceSpec( walmart ).matchesOrSubsumedBy( walmart, false, null ) );
        assertFalse( new ResourceSpec( bob ).matchesOrSubsumedBy( Actor.UNKNOWN, false, null ) );
        assertFalse( new ResourceSpec( walmart ).matchesOrSubsumedBy( mas, false, null ) );
        assertFalse( new ResourceSpec( peon ).matchesOrSubsumedBy( janitor, false, null ) );
        assertFalse( new ResourceSpec( building ).matchesOrSubsumedBy( Place.UNKNOWN, false, null ) );
    }


    @Test
    public void testMatchesOrSubsumes() {
        assertFalse( new ResourceSpec( walmart ).matchesOrSubsumes( mas, true, null ) );
        assertTrue( new ResourceSpec( cafeteria ).matchesOrSubsumes( cafeteria, true, null ) );
        assertTrue( new ResourceSpec( walmart ).matchesOrSubsumes( walmart, false, null ) );
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


    /**
     * ModelEntity tests... Here to avoid duplication of test data...
     */
    @Test
    public void narrowsOrEqualTest() {
        assertFalse( mas.narrowsOrEquals( bob, null ) );

        Place loop1 = new Place( "loop1" );
        modelDao.add( loop1 );
        Place loop2 = new Place( "loop2" );
        modelDao.add( loop2 );
        loop1.setWithin( loop2 );
        loop2.setWithin( loop1 );
        assertTrue( loop1.isInvalid( null ) );
        assertTrue( loop2.isInvalid( null ) );
        assertTrue( loop1.narrowsOrEquals( loop1, null ) );
        assertFalse( loop1.narrowsOrEquals( null, null ) );
        assertFalse( loop1.narrowsOrEquals( loop2, null ) );
        assertFalse( loop2.narrowsOrEquals( loop1, null ) );
        assertFalse( loop2.narrowsOrEquals( mas, null ) );

        assertTrue( guy.narrowsOrEquals( person, null ) );
        assertFalse( person.narrowsOrEquals( guy, null ) );
        assertFalse( gal.narrowsOrEquals( guy, null ) );
        assertFalse( guy.narrowsOrEquals( gal, null ) );
        assertFalse( mas.narrowsOrEquals( gal, null ) );
        assertFalse( mas.narrowsOrEquals( null, null ) );

        assertTrue( njCo.narrowsOrEquals( company, null ) );
        assertFalse( njCo.narrowsOrEquals( nyCo, null ) );

        // njite, company, njCo
    }

    @Test
    public void testImplies() {
        assertTrue( ModelEntity.implies( null, null, null ) );
        assertTrue( ModelEntity.implies( njCo, company, null ) );
    }

    @Test
    public void testValidates() {
        assertTrue( company.validates( mas, null ) );
        assertFalse( company.validates( null, null ) );
        assertFalse( company.validates( Organization.UNKNOWN, null ) );
        assertFalse( mas.validates( company, null ) );

        assertFalse( cafeteria.validates( mas, null ) );

        building.getMustContain().setPlace( cafeteria );
        cafeteria.getMustBeContainedIn().setPlace( building );
        Place office = new Place( "Office" );
        office.setActual();
        modelDao.add( office );
        office.addType( building );

        assertTrue( building.validates( office, null ) );
        assertFalse( building.validates( cafeteria, null ) );
        assertTrue( cafeteria.narrowsOrEquals( building, null ) );

        Place resto = new Place( "Restaurant" );
        modelDao.add( resto );
        resto.setActual();
        resto.addType( cafeteria );

        assertFalse( cafeteria.validates( resto, null ) );
        resto.setWithin( office );

        assertTrue( building.validates( office, null ) );
        assertTrue( cafeteria.validates( resto, null ) );
    }


}
