package com.mindalliance.channels;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Test generic node functionality
 */
public class TestNode extends TestCase {

    private Node connector;
    private Flow f1;
    private Flow f2;
    private Part p1;
    private Part p2;
    private Scenario scenario;

    public TestNode() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        scenario = new Scenario();
        p1 = new Part( new Actor( "bob" ), "p1" );
        connector = new Connector();
        p2 = new Part( new Actor( "joe" ), "p3" );
        scenario.addNode( p1 );
        scenario.addNode( connector );
        scenario.addNode( p2 );
        f1 = scenario.connect( p1, connector );
        f1.setName( "A" );
        f2 = scenario.connect( connector, p2 );
        f2.setName( "B" );
    }

    public void testSetOutcomes() {
        final Set<Flow> flows = new HashSet<Flow>(4);
        flows.add( f1 );
        flows.add( f2 );

        connector.setOutcomes( flows );
        assertSame( f1, connector.getFlow( f1.getId() ) );
        assertSame( connector, f1.getSource() );
        assertSame( f2, connector.getFlow( f2.getId() ) );
        assertSame( connector, f2.getSource() );
    }

    public void testSetRequirements() {
        final Set<Flow> flows = new HashSet<Flow>(4);
        flows.add( f1 );
        flows.add( f2 );

        connector.setRequirements( flows );
        assertSame( f1, connector.getFlow( f1.getId() ) );
        assertSame( connector, f1.getTarget() );
        assertSame( f2, connector.getFlow( f2.getId() ) );
        assertSame( connector, f2.getTarget() );
    }

    public void testOutcomes() {
        final Iterator<Flow> iterator1 = connector.outcomes();
        assertTrue( iterator1.hasNext() );
        assertSame( p2, iterator1.next().getTarget() );
        assertFalse( iterator1.hasNext() );

        assertFalse( p2.outcomes().hasNext() );
    }

    public void testRequirements() {
        final Iterator<Flow> iterator1 = connector.requirements();
        assertTrue( iterator1.hasNext() );
        assertSame( p1, iterator1.next().getSource() );
        assertFalse( iterator1.hasNext() );

        assertFalse( p1.requirements().hasNext() );
    }

    public void testGetFlow() {
        assertSame( f1, connector.getFlow( f1.getId() ) );
        assertSame( f2, connector.getFlow( f2.getId() ) );
        assertNull( p1.getFlow( f2.getId() ) );
        assertNull( p2.getFlow( f1.getId() ) );
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
