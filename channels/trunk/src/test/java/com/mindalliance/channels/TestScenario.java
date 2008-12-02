package com.mindalliance.channels;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

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
        scenario = Scenario.createDefault();
    }

    public void testDescription() {
        assertEquals( Scenario.DEFAULT_DESCRIPTION, scenario.getDescription() );
        scenario.setDescription( null );
        assertEquals( "", scenario.getDescription() );

        final String s = "Bla";
        scenario.setDescription( s );
        assertSame( s, scenario.getDescription() );
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
        int size = scenario.getNodeCount();
        scenario.addNode( p1 );
        assertEquals( size+1, scenario.getNodeCount() );
        assertSame( p1, scenario.getNode( p1.getId() ) );

        scenario.removeNode( p1 );
        assertEquals( size, scenario.getNodeCount() );
        assertNull( scenario.getNode( p1.getId() ) );

        try {
            scenario.setNodes( new HashSet<Node>(0) );
            fail();
        } catch ( IllegalArgumentException ignored ) {}
    }

    public void testRemoveOnly() {
        final Iterator<Node> nodes = scenario.nodes();
        assertTrue( nodes.hasNext() );
        final Node initial = nodes.next();
        assertSame( initial, scenario.getNode( initial.getId() ) );

        scenario.removeNode( initial );
        assertSame( initial, scenario.getNode( initial.getId() ) );
    }

    public void testSetNodes() {
        final Set<Node> ps = new HashSet<Node>( 2 );
        final Part p1 = new Part();
        ps.add( p1 );
        final Node p2 = new ScenarioNode( new Scenario() );
        ps.add( p2 );
        assertNull( p1.getScenario() );
        assertNull( p2.getScenario() );
        scenario.setNodes( ps );
        assertSame( p1, scenario.getNode( p1.getId() ) );
        assertSame( p2, scenario.getNode( p2.getId() ) );
        assertSame( scenario, p1.getScenario() );
        assertSame( scenario, p2.getScenario() );
    }

    public void testConnect() {
        final Part p1 = new Part();
        final Part p2 = new Part();

        try {
            scenario.connect( p1, p2 );
            fail();
        } catch ( IllegalArgumentException ignored ) {}

        scenario.addNode( p1 );
        scenario.addNode( p2 );

        final Flow f = scenario.connect( p1, p2 );
        assertSame( p1, f.getSource() );
        assertSame( p2, f.getTarget() );
        assertSame( f, p1.getFlow( f.getId() ) );
        assertSame( f, p2.getFlow( f.getId() ) );
        assertSame( f, p1.outcomes().next() );
        assertSame( f, p2.requirements().next() );

        try {
            scenario.connect( p1, p2 );
            fail();
        } catch ( IllegalArgumentException ignored ) {}
    }

    public void testDisconnect() {
        final Part p1 = new Part();
        final Part p2 = new Part();
        scenario.addNode( p1 );
        scenario.addNode( p2 );

        final Flow f = scenario.connect( p1, p2 );

        assertSame( f, p1.getFlow( f.getId() ) );
        assertSame( f, p2.getFlow( f.getId() ) );
        scenario.disconnect( p1, p2 );
        assertNull( p1.getFlow( f.getId() ) );
        assertNull( p2.getFlow( f.getId() ) );
    }

    public void testFlows() {
        final Part bob = new Part( new Actor( "Bob" ), "talking" );
        final Part sue = new Part( new Actor( "Sue" ), "repeating" );
        final Part joe = new Part( new Actor( "Joe" ), "zoning out" );
        scenario.addNode( bob );
        scenario.addNode( sue );
        scenario.addNode( joe );

        Flow f1 = scenario.connect( bob, sue );
        Flow f2 = scenario.connect( bob, joe );
        Flow f3 = scenario.connect( sue, joe );
        Flow f4 = scenario.connect( joe, bob );

        Iterator<Flow> flows = scenario.flows();
        assertTrue( flows.hasNext() );
        assertSame( f1, flows.next() );
        assertSame( f2, flows.next() );
        assertSame( f4, flows.next() );
        assertSame( f3, flows.next() );

        assertFalse( flows.hasNext() );

        try {
            flows.next();
            fail();
        } catch ( NoSuchElementException e ) {}

        try {
            scenario.flows().remove();
            fail();
        } catch ( UnsupportedOperationException e ) {}
    }
}
