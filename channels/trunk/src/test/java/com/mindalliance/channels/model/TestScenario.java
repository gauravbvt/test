package com.mindalliance.channels.model;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

/**
 * Test a scenario in isolation.
 */
public class TestScenario extends TestCase {

    /** The scenario being tested. */
    private Scenario scenario;

    public TestScenario() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        scenario = new Scenario();
    }

    public void testDescription() {
        assertEquals( Scenario.DEFAULT_DESCRIPTION, scenario.getDescription() );
        try {
            scenario.setDescription( null );
            fail();
        } catch ( IllegalArgumentException ignored ) {
            final String s = "Bla";
            scenario.setDescription( s );
            assertSame( s, scenario.getDescription() );
        }

    }

    public void testName() {
        assertEquals( Scenario.DEFAULT_NAME, scenario.getName() );
        try {
            scenario.setName( null );
            fail();
        } catch ( IllegalArgumentException ignored ) {
            final String s = "Bla";
            scenario.setName( s );
            assertSame( s, scenario.getName() );
        }
    }

    public void testEquals() {
        assertEquals( scenario, scenario );
        assertFalse( scenario.equals( null ) );
        assertFalse( scenario.equals( new Scenario() ) );
    }

    public void testHashCode() {
        assertNotSame( scenario.hashCode(), new Scenario().hashCode() );
    }

    public void testNodes() {

        final Node p1 = new Connector();
        assertNull( scenario.getNode( p1.getId() ) );
        scenario.addNode( p1 );
        assertSame( p1, scenario.getNode( p1.getId() ) );

        scenario.removeNode( p1 );
        assertNull( scenario.getNode( p1.getId() ) );

        try {
            scenario.setNodes( new HashSet<Node>(0) );
            fail();
        } catch ( IllegalArgumentException ignored ) {}
    }

    public void testRemoveOnly() {
        final Iterator<Node> nodes = scenario.nodes();
        assertTrue( nodes.hasNext() );
        Node initial = nodes.next();
        assertSame( initial, scenario.getNode( initial.getId() ) );

        scenario.removeNode( initial );
        assertSame( initial, scenario.getNode( initial.getId() ) );
    }

    public void testSetNodes() {
        final Set<Node> ps = new HashSet<Node>( 2 );
        final Part p1 = new Part();
        ps.add( p1 );
        final Node p2 = new ScenarioNode();
        ps.add( p2 );
        scenario.setNodes( ps );
        assertSame( p1, scenario.getNode( p1.getId() ) );
        assertSame( p2, scenario.getNode( p2.getId() ) );
    }
}
