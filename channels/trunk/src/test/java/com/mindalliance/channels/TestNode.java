package com.mindalliance.channels;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ScenarioNode;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Node;

/**
 * Test generic node functionality
 */
public class TestNode extends TestCase {

    private Node node;
    private Flow a;
    private Flow b;

    public TestNode() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        node = new Connector();
        a = new Flow();
        a.setName( "A" );
        b = new Flow();
        b.setName( "B" );
    }

    public void testSetOutcomes() {
        final Set<Flow> flows = new HashSet<Flow>(4);
        flows.add( a );
        flows.add( b );

        node.setOutcomes( flows );
        assertSame( a, node.getFlow( a.getId() ) );
        assertSame( node, a.getSource() );
        assertSame( b, node.getFlow( b.getId() ) );
        assertSame( node, b.getSource() );
    }

    public void testSetRequirements() {
        final Set<Flow> flows = new HashSet<Flow>(4);
        flows.add( a );
        flows.add( b );

        node.setRequirements( flows );
        assertSame( a, node.getFlow( a.getId() ) );
        assertSame( node, a.getTarget() );
        assertSame( b, node.getFlow( b.getId() ) );
        assertSame( node, b.getTarget() );
    }

    public void testOutcomes() {
        assertFalse( node.outcomes().hasNext() );

        node.addOutcome( b );
        final Iterator<Flow> iterator = node.outcomes();
        assertTrue( iterator.hasNext() );
        assertSame( b, iterator.next() );
        assertFalse( iterator.hasNext() );

        node.addOutcome( a );
        final Iterator<Flow> iterator2 = node.outcomes();
        assertTrue( iterator2.hasNext() );
        assertSame( a, iterator2.next() );
        assertSame( b, iterator2.next() );
        assertFalse( iterator2.hasNext() );

        node.removeOutcome( b );
        final Iterator<Flow> iterator3 = node.outcomes();
        assertTrue( iterator3.hasNext() );
        assertSame( a, iterator3.next() );
        assertFalse( iterator3.hasNext() );

        node.removeOutcome( a );
        assertFalse( node.outcomes().hasNext() );
    }

    public void testRequirements() {
        assertFalse( node.requirements().hasNext() );

        node.addRequirement( b );
        final Iterator<Flow> iterator = node.requirements();
        assertTrue( iterator.hasNext() );
        assertSame( b, iterator.next() );
        assertFalse( iterator.hasNext() );

        node.addRequirement( a );
        final Iterator<Flow> iterator2 = node.requirements();
        assertTrue( iterator2.hasNext() );
        assertSame( a, iterator2.next() );
        assertSame( b, iterator2.next() );
        assertFalse( iterator2.hasNext() );

        node.removeRequirement( b );
        final Iterator<Flow> iterator3 = node.requirements();
        assertTrue( iterator3.hasNext() );
        assertSame( a, iterator3.next() );
        assertFalse( iterator3.hasNext() );

        node.removeRequirement( a );
        assertFalse( node.requirements().hasNext() );
    }

    public void testGetFlow() {
        assertNull( node.getFlow( a.getId() ) );

        node.addOutcome( a );
        assertSame( a, node.getFlow( a.getId() ) );
        node.removeOutcome( a );
        assertNull( node.getFlow( a.getId() ) );

        node.addRequirement( a );
        assertSame( a, node.getFlow( a.getId() ) );
        node.removeRequirement( a );
        assertNull( node.getFlow( a.getId() ) );
    }

    public void testIsness() {
        final Part part = new Part( new Actor(), "" );
        assertTrue( part.isPart() );
        assertFalse( part.isConnector() );
        assertFalse( part.isScenarioNode() );

        final Connector connector = new Connector();
        assertFalse( connector.isPart() );
        assertTrue( connector.isConnector() );
        assertFalse( connector.isScenarioNode() );

        final ScenarioNode sn = new ScenarioNode();
        assertFalse( sn.isPart() );
        assertFalse( sn.isConnector() );
        assertTrue( sn.isScenarioNode() );
    }
}
