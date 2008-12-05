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

        scenario.setName( null );
        assertEquals( "", scenario.getName() );

        final String s = "Bla";
        scenario.setName( s );
        assertSame( s, scenario.getName() );
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
        Part p2 = new Part();
        Part p3 = new Part();


        assertNull( scenario.getNode( p1.getId() ) );
        assertNull( scenario.getNode( p2.getId() ) );
        assertNull( scenario.getNode( p3.getId() ) );
        final int size = scenario.getNodeCount();

        scenario.addNode( p1 );
        scenario.addNode( p2 );
        scenario.addNode( p3 );
        final Flow f1 = scenario.connect( p1, p2 );
        final Flow f2 = scenario.connect( p2, p3 );

        assertEquals( size+3, scenario.getNodeCount() );
        assertSame( p1, scenario.getNode( p1.getId() ) );
        assertSame( f1, p1.getFlow( f1.getId() ) );
        assertSame( f1, p2.getFlow( f1.getId() ) );
        assertSame( f2, p2.getFlow( f2.getId() ) );
        assertSame( f2, p3.getFlow( f2.getId() ) );
        scenario.removeNode( p2 );

        assertEquals( size+2, scenario.getNodeCount() );
        assertNull( scenario.getNode( p2.getId() ) );
        assertNull( p1.getFlow( f1.getId() ) );
        assertNull( p3.getFlow( f2.getId() ) );

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

        Set<Flow> fs = new HashSet<Flow>();
        fs.add( f1 );
        fs.add( f2 );
        fs.add( f3 );
        fs.add( f4 );

        Iterator<Flow> flows = scenario.flows();
        assertTrue( flows.hasNext() );
        assertTrue( fs.contains( flows.next() ) );
        assertTrue( fs.contains( flows.next() ) );
        assertTrue( fs.contains( flows.next() ) );
        assertTrue( fs.contains( flows.next() ) );

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
