package com.mindalliance.channels.model;

import junit.framework.TestCase;

public class TestPart extends TestCase {

    private Part part;

    public TestPart() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        part = new Part();
    }

    public void testTask() {
        assertSame( Part.DEFAULT_TASK, part.getTask() );
        part.setTask( null );
        assertSame( Part.DEFAULT_TASK, part.getTask() );
        part.setTask( "" );
        assertSame( Part.DEFAULT_TASK, part.getTask() );

        final String s = "Bla";
        part.setTask( s );
        assertSame( s, part.getTask() );

    }

    public void testToString() {
        assertEquals( "Unknown actor doing something", part.toString() );

        part.setTask( "standing by" );
        assertEquals( "Unknown actor standing by", part.toString() );

        final Person bob = new Person();
        bob.setName( "Bob" );
        part.setActor( bob );
        assertEquals( "Bob standing by", part.toString() );

        Role role = new Role();
        role.setName( "Operator" );
        part.setRole( role );
        assertEquals( "Bob standing by", part.toString() );

        part.setActor( null );
        assertEquals( "Operator standing by", part.toString() );
    }

    // ================ Bogus tests for coverage

    public void testOrganization() {
        assertNull( part.getOrganization() );
        final Organization org = new Organization();
        org.setName( "Org" );
        part.setOrganization( org );
        assertSame( org, part.getOrganization() );
    }

    public void testLocation() {
        assertNull( part.getLocation() );
        final Location location = new Location();
        location.setName( "Somewhere" );
        part.setLocation( location );
        assertSame( location, part.getLocation() );
    }

    public void testJurisdiction() {
        assertNull( part.getJurisdiction() );
        final Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setName( "Somewhere" );
        part.setJurisdiction( jurisdiction );
        assertSame( jurisdiction, part.getJurisdiction() );
    }
}
