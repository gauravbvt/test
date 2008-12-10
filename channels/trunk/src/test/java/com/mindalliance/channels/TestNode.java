package com.mindalliance.channels;

import com.mindalliance.channels.dao.Memory;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Test generic node functionality
 */
public class TestNode extends TestCase {

    private Flow f1;
    private Flow f2;
    private Flow f3;
    private Part p1;
    private Part p2;
    private Scenario scenario;

    public TestNode() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        scenario = new Memory().createScenario();

        p1 = scenario.createPart();
            p1.setActor( new Actor( "p1" ) );

        p2 = scenario.createPart();
            p2.setActor( new Actor( "p2" ) );

        f1 = p1.createOutcome();
                f1.setName( "A" );
        f2 = p2.createRequirement();
                f2.setName( "B" );
        f3 = scenario.connect( p1, p2 );
                f3.setName( "message" );
    }

    public void testSetOutcomes() {
        p1.setOutcomes( new HashSet<Flow>() );
        assertFalse( p1.outcomes().hasNext() );
        assertNull( f1.getSource() );
        assertNull( f3.getSource() );

        final Set<Flow> flows = new HashSet<Flow>(4);
        flows.add( f1 );
        flows.add( f2 );
        p1.setOutcomes( flows );
        assertSame( f1, p1.getFlow( f1.getId() ) );
        assertSame( p1, f1.getSource() );
        assertSame( p1, f2.getSource() );
        assertSame( f2, p1.getFlow( f2.getId() ) );
        assertNull( p1.getFlow( f3.getId() ) );
    }

    public void testSetRequirements() {
        p2.setRequirements( new HashSet<Flow>() );
        assertFalse( p2.requirements().hasNext() );
        assertNull( f2.getTarget() );
        assertNull( f3.getTarget() );

        final Set<Flow> flows = new HashSet<Flow>(4);
        flows.add( f1 );
        flows.add( f2 );
        p2.setRequirements( flows );
        assertSame( f1, p2.getFlow( f1.getId() ) );
        assertSame( p2, f1.getTarget() );
        assertSame( p2, f2.getTarget() );
        assertSame( f2, p2.getFlow( f2.getId() ) );
        assertNull( p2.getFlow( f3.getId() ) );
    }

    public void testOutcomes() {
        final Iterator<Flow> iterator1 = p1.outcomes();
        assertTrue( iterator1.hasNext() );
        iterator1.next();
        iterator1.next();
        assertFalse( iterator1.hasNext() );

        assertFalse( p2.outcomes().hasNext() );
    }

    public void testRequirements() {
        final Iterator<Flow> iterator1 = p2.requirements();
        assertTrue( iterator1.hasNext() );
        iterator1.next();
        iterator1.next();
        assertFalse( iterator1.hasNext() );

        assertFalse( p1.requirements().hasNext() );
    }

    public void testIsness() {
        final Part part = scenario.createPart();
        assertTrue( part.isPart() );
        assertFalse( part.isConnector() );

        final Connector c = scenario.createConnector();
        assertFalse( c.isPart() );
        assertTrue( c.isConnector() );
    }
}
