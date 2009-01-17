package com.mindalliance.channels;

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
        assertEquals( "Somebody doing something", part.toString() );

        part.setTask( "standing by" );
        assertEquals( "Somebody standing by", part.toString() );

        final Actor bob = new Actor( "Bob" );
        part.setActor( bob );
        assertEquals( "Bob standing by", part.toString() );

        final Role role = new Role();
        role.setName( "Operator" );
        part.setRole( role );
        assertEquals( "Bob standing by", part.toString() );

        part.setActor( null );
        assertEquals( "Operator standing by", part.toString() );

        part.setOrganization( new Organization( "Verizon" ) );
        assertEquals( "Operator standing by", part.toString() );
        part.setRole( null );
        assertEquals( "Verizon standing by", part.toString() );

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
        final Place location = new Place( "Somewhere" );
        part.setLocation( location );
        assertSame( location, part.getLocation() );
    }

    public void testJurisdiction() {
        assertNull( part.getJurisdiction() );
        final Place jurisdiction = new Place( "Somewhere" );
        part.setJurisdiction( jurisdiction );
        assertSame( jurisdiction, part.getJurisdiction() );
    }

    public void testName() {
        assertSame( Part.DEFAULT_ACTOR, part.getName() );

        part.setName( "task" );
        assertSame( "task", part.getName() );

        final String s = "MAS";
        part.setOrganization( new Organization( s ) );
        assertSame( s, part.getName() );

        final String s1 = "Guru";
        part.setRole( new Role( s1 ) );
        assertSame( s1, part.getName() );

        final String s2 = "HAL 9000";
        final Actor actor = new Actor( s2 );
        part.setActor( actor );
        assertSame( s2, part.getName() );

        part.setActor( null );
        assertSame( s1, part.getName() );

        part.setRole( null );
        assertSame( s, part.getName() );
        
        part.setOrganization( null );
        assertSame( Part.DEFAULT_ACTOR, part.getName() );
    }

    public void testIsSystem() {
        assertFalse( part.isSystem() );
        part.setRole( new Role( "System" ) );
        assertTrue( part.isSystem() );

        part.setRole( new Role( "Computer system" ) );
        assertTrue( part.isSystem() );
    }
}
