package com.mindalliance.channels.model;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Test a scenario in isolation.
 */
public class TestScenario extends TestCase {

    private Scenario scenario;

    @Override
    protected void setUp() {
        scenario = new Scenario();
    }

    public void testDescription() {
        assertEquals( "", scenario.getDescription() );
        try {
            scenario.setDescription( null );
            fail();
        } catch ( IllegalArgumentException ignored ) {}
        String s = "Bla";
        scenario.setDescription( s );
        assertSame( s, scenario.getDescription() );
    }

    public void testName() {
        assertEquals( "", scenario.getName() );
        try {
            scenario.setName( null );
            fail();
        } catch ( IllegalArgumentException ignored ) {}
        String s = "Bla";
        scenario.setName( s );
        assertSame( s, scenario.getName() );
    }

    public void testEquals() {
        assertTrue( scenario.equals( scenario ) );
        assertFalse( scenario.equals( "bla" ) );
        assertFalse( scenario.equals( new Scenario() ) );
    }

    public void testHashCode() {
        assertFalse( scenario.hashCode() == new Scenario().hashCode() );
    }

    public void testParts() {
        assertFalse( scenario.parts().hasNext() );

        Part p1 = new Part();

        assertNull( scenario.getPart( p1.getId() ) );
        scenario.addPart( p1 );
        assertSame( p1, scenario.getPart( p1.getId() ) );
        Iterator<Part> iterator = scenario.parts();
        assertTrue( iterator.hasNext() );
        assertSame( p1, iterator.next() );

        scenario.removePart( p1 );
        assertNull( scenario.getPart( p1.getId() ) );
        assertFalse( scenario.parts().hasNext() );
    }

    public void testSetParts() {
        Set<Part> ps = new HashSet<Part>();
        Part p1 = new Part();
        ps.add( p1 );
        Part p2 = new Part();
        ps.add( p2 );

        scenario.setParts( ps );
        assertSame( p1, scenario.getPart( p1.getId() ) );
        assertSame( p2, scenario.getPart( p2.getId() ) );
    }
}
