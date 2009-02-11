package com.mindalliance.channels;

import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.service.ChannelsServiceImpl;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Test generic node functionality
 */
@SuppressWarnings( { "HardCodedStringLiteral" } )
public class TestNode extends TestCase {

    private Flow f1;
    private Flow f2;
    private Flow f3;
    private Part p1;
    private Part p2;
    private Scenario scenario;
    private ChannelsServiceImpl service;

    public TestNode() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        service = new ChannelsServiceImpl( new Memory() );
        scenario = service.createScenario();
        p1 = service.createPart( scenario );
            p1.setActor( service.findOrCreate( Actor.class, "p1" ) );
        p2 = service.createPart( scenario );
            p2.setActor( service.findOrCreate( Actor.class, "p2" ) );

        f1 = p1.createOutcome( service );
                f1.setName( "A" );
        f2 = p2.createRequirement( service );
                f2.setName( "B" );
        f3 = service.connect( p1, p2, "message" );
    }

    public void testSetOutcomes() {
        p1.setOutcomes( new HashMap<Long,Flow>() );
        assertFalse( p1.outcomes().hasNext() );
        assertNull( f1.getSource() );
        assertNull( f3.getSource() );

        Map<Long,Flow> flows = new HashMap<Long,Flow>();
        flows.put( f1.getId(), f1 );
        flows.put( f2.getId(), f2 );
        p1.setOutcomes( flows );
        assertSame( f1, p1.getFlow( f1.getId() ) );
        assertSame( p1, f1.getSource() );
        assertSame( p1, f2.getSource() );
        assertSame( f2, p1.getFlow( f2.getId() ) );
        assertNull( p1.getFlow( f3.getId() ) );
    }

    public void testSetRequirements() {
        p2.setRequirements( new HashMap<Long,Flow>() );
        assertFalse( p2.requirements().hasNext() );
        assertNull( f2.getTarget() );
        assertNull( f3.getTarget() );

        Map<Long,Flow> flows = new HashMap<Long,Flow>();
        flows.put( f1.getId(), f1 );
        flows.put( f2.getId(), f2 );
        p2.setRequirements( flows );
        assertSame( f1, p2.getFlow( f1.getId() ) );
        assertSame( p2, f1.getTarget() );
        assertSame( p2, f2.getTarget() );
        assertSame( f2, p2.getFlow( f2.getId() ) );
        assertNull( p2.getFlow( f3.getId() ) );
    }

    public void testOutcomes() {
        Iterator<Flow> iterator1 = p1.outcomes();
        assertTrue( iterator1.hasNext() );
        iterator1.next();
        iterator1.next();
        assertFalse( iterator1.hasNext() );

        assertFalse( p2.outcomes().hasNext() );
    }

    public void testRequirements() {
        Iterator<Flow> iterator1 = p2.requirements();
        assertTrue( iterator1.hasNext() );
        iterator1.next();
        iterator1.next();
        assertFalse( iterator1.hasNext() );

        assertFalse( p1.requirements().hasNext() );
    }

    public void testIsness() {
        Part part = service.createPart( scenario );
        assertTrue( part.isPart() );
        assertFalse( part.isConnector() );
        Connector c = service.createConnector( scenario );
        assertFalse( c.isPart() );
        assertTrue( c.isConnector() );
    }
}
