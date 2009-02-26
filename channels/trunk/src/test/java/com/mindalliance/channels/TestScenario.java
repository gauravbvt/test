package com.mindalliance.channels;

import com.mindalliance.channels.service.ChannelsServiceImpl;
import com.mindalliance.channels.dao.Memory;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Test a scenario in isolation.
 */
@SuppressWarnings( { "OverlyLongMethod", "HardCodedStringLiteral" } )
public class TestScenario extends TestCase {

    /** The scenario being tested. */
    private Scenario scenario;
    private Service service;

    public TestScenario() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        service = new ChannelsServiceImpl( new Memory() );
        scenario = service.createScenario();
    }

    public void testDescription() {
        assertEquals( Scenario.DEFAULT_DESCRIPTION, scenario.getDescription() );
        scenario.setDescription( null );
        assertEquals( "", scenario.getDescription() );

        String s = "Bla";
        scenario.setDescription( s );
        assertSame( s, scenario.getDescription() );
    }

    public void testName() {
        assertEquals( Scenario.DEFAULT_NAME, scenario.getName() );

        scenario.setName( null );
        assertEquals( "", scenario.getName() );

        String s = "Bla";
        scenario.setName( s );
        assertSame( s, scenario.getName() );
    }

    public void testEquals() {
        assertEquals( scenario, scenario );
        assertFalse( scenario.equals( null ) );
        assertFalse( scenario.equals( service.createScenario() ) );
    }

    public void testHashCode() {
        assertNotSame( scenario.hashCode(), service.createScenario().hashCode() );
    }

    public void testNodes() {
        assertEquals( 1, scenario.getNodeCount() );

        Node p1 = scenario.getDefaultPart();
        Part p2 = service.createPart( scenario );
        Part p3 = service.createPart( scenario );

        assertSame( p1, scenario.getNode( p1.getId() ) );
        assertSame( p2, scenario.getNode( p2.getId() ) );
        assertSame( p3, scenario.getNode( p3.getId() ) );

        Flow f1 = service.connect( p1, p2, "" );
        Flow f2 = service.connect( p2, p3, "" );

        assertEquals( 3, scenario.getNodeCount() );
        assertSame( f1, p1.getFlow( f1.getId() ) );
        assertSame( f1, p2.getFlow( f1.getId() ) );
        assertSame( f2, p2.getFlow( f2.getId() ) );
        assertSame( f2, p3.getFlow( f2.getId() ) );

        scenario.removeNode( p2 );
        // node replaced in flows by connectors
        assertEquals( 4, scenario.getNodeCount() );
        assertSame( p1, scenario.getNode( p1.getId() ) );
        assertNull( scenario.getNode( p2.getId() ) );
        assertSame( p3, scenario.getNode( p3.getId() ) );

        assertNull( p1.getFlow( f1.getId() ) );
        assertNull( p3.getFlow( f2.getId() ) );
        assertNull( f1.getSource() );
        assertNull( f2.getSource() );
        assertNull( f1.getTarget() );
        assertNull( f2.getTarget() );
    }

    public void testRemoveOnly() {
        Iterator<Node> nodes = scenario.nodes();
        assertTrue( nodes.hasNext() );
        Node initial = nodes.next();
        assertSame( initial, scenario.getNode( initial.getId() ) );

        scenario.removeNode( initial );
        assertSame( initial, scenario.getNode( initial.getId() ) );
    }

    public void testConnect() {
        Part p1 = service.createPart( scenario );
        Part p2 = service.createPart( scenario );

        service.connect( p1, p2, "" );
        Part p3 = service.createPart( scenario );
        Part p4 = service.createPart( scenario );

        Flow f = service.connect( p3, p4, "" );
        assertSame( p3, f.getSource() );
        assertSame( p4, f.getTarget() );
        assertSame( f, p3.getFlow( f.getId() ) );
        assertSame( f, p4.getFlow( f.getId() ) );
        assertSame( f, p3.outcomes().next() );
        assertSame( f, p4.requirements().next() );

        try {
            service.connect( p3, p4, "" );
            fail();
        } catch ( IllegalArgumentException ignored ) {}
    }

    public void testDisconnect() {
        Part p1 = service.createPart( scenario );
        Part p2 = service.createPart( scenario );

        Flow f = service.connect( p1, p2, "" );

        assertSame( f, p1.getFlow( f.getId() ) );
        assertSame( f, p2.getFlow( f.getId() ) );
        f.disconnect();
        assertNull( p1.getFlow( f.getId() ) );
        assertNull( p2.getFlow( f.getId() ) );
    }

    public void testFlows() {
        Part bob = service.createPart( scenario );
        Part sue = service.createPart( scenario );
        Part joe = service.createPart( scenario );

        Flow f1 = service.connect( bob, sue, "" );
        Flow f2 = service.connect( bob, joe, "" );
        Flow f3 = service.connect( sue, joe, "" );
        Flow f4 = service.connect( joe, bob, "" );

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
        } catch ( NoSuchElementException ignored ) {}

        try {
            scenario.flows().remove();
            fail();
        } catch ( UnsupportedOperationException ignored ) {}
    }

    public void testDeleteLast() {
        Part dp = scenario.getDefaultPart();

        Iterator<Node> nodes = scenario.nodes();
        assertSame( dp, nodes.next() );
        assertFalse( nodes.hasNext() );

        scenario.removeNode( dp );
        Iterator<Node> nodes2 = scenario.nodes();
        assertSame( dp, nodes2.next() );
        assertFalse( nodes2.hasNext() );

        Flow out = dp.createOutcome( service );
        Connector c1 = (Connector) out.getTarget();
        Flow in  = dp.createRequirement( service );
        Connector c2 = (Connector) in.getSource();
        scenario.removeNode( dp );

        assertSame( dp, scenario.getNode( dp.getId() ) );
        assertSame( c1, scenario.getNode( c1.getId() ) );
        assertSame( c2, scenario.getNode( c2.getId() ) );
        Part p2 = service.createPart( scenario );
        scenario.removeNode( dp );
        assertSame( p2, scenario.getNode( p2.getId() ) );
        assertNull( scenario.getNode( dp.getId() ) );
        assertNull( scenario.getNode( c1.getId() ) );
        assertNull( scenario.getNode( c2.getId() ) );

        scenario.removeNode( p2 );
        assertSame( p2, scenario.getNode( p2.getId() ) );
    }
}
