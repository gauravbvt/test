package com.mindalliance.channels.model;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
        node = new System();
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
        assertSame( b, node.getFlow( b.getId() ) );
    }

    public void testSetRequirements() {
        final Set<Flow> flows = new HashSet<Flow>(4);
        flows.add( a );
        flows.add( b );

        node.setRequirements( flows );
        assertSame( a, node.getFlow( a.getId() ) );
        assertSame( b, node.getFlow( b.getId() ) );
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
}
