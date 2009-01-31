package com.mindalliance.channels;

import com.mindalliance.channels.service.ChannelsServiceImpl;
import com.mindalliance.channels.dao.Memory;
import junit.framework.TestCase;

/**
 * ...
 */
public class TestExternalFlow extends TestCase {

    private Service service;
    private Scenario s1;
    private Scenario s2;

    private Part s1p1;
    private Part s1p2;

    public TestExternalFlow() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        service = new ChannelsServiceImpl( new Memory() );
        s1 = service.createScenario();
        s1p1 = s1.getDefaultPart();
        s1p1.setActor( new Actor( "p1" ) );
        s1p2 = service.createPart( s1 );
        s1p2.setActor( new Actor( "p2" ) );

        s2 = service.createScenario();

        // S2 "included" in S1
        final Part s2Part = s2.getDefaultPart();
        s2Part.setActor( new Actor( "p3" ) );
        s2Part.createOutcome( service );
        s2Part.createRequirement( service );

        s1.connect( s1p1, s2.inputs().next() );
        s1.connect( s2.outputs().next(), s1p2 );
    }

    public void testConstructor() {
        // test degenerate cases
        try {
            final Part p = s2.getDefaultPart();
            new ExternalFlow( p, p.outcomes().next().getTarget() );
            fail();
        } catch ( IllegalArgumentException ignored ) {}

        try {
            new ExternalFlow( s1p1, s1p2 );
            fail();
        } catch ( IllegalArgumentException ignored ) {}
    }

    public void testInitial() {
        final Flow f1 = s1p1.outcomes().next();
        assertFalse( f1.isInternal() );
        assertEquals( "p1 notify p3 of something", f1.toString() );
        assertTrue( s2.inputs().next().externalFlows().hasNext() );

        final Flow f2 = s1p2.requirements().next();
        assertFalse( f2.isInternal() );
        assertEquals( "p3 notify p2 of something", f2.toString() );
        assertTrue( s2.outputs().next().externalFlows().hasNext() );
    }

    /**
     * Remove an external flow.
     */
    public void testRemove1() {
        // output
        final Flow f = s1p1.outcomes().next();
        f.disconnect();

        assertNull( f.getSource() );
        assertNull( f.getTarget() );
        assertFalse( s1p1.outcomes().hasNext() );
        assertFalse( s2.inputs().next().externalFlows().hasNext() );

        // input
        final Flow f2 = s1p2.requirements().next();
        f2.disconnect();

        assertNull( f2.getSource() );
        assertNull( f2.getTarget() );
        assertFalse( s1p2.requirements().hasNext() );
        assertFalse( s2.outputs().next().externalFlows().hasNext() );
    }

    /**
     * Remove a connected connector.
     */
    public void testRemove2() {
        final Part p3 = s2.getDefaultPart();
        final Flow f1 = p3.outcomes().next();
        final Connector c1 = (Connector) f1.getTarget();

        f1.disconnect();

        assertFalse( c1.requirements().hasNext() );
        assertFalse( c1.externalFlows().hasNext() );
        assertFalse( s1p2.requirements().hasNext() );

        final Flow f2 = p3.requirements().next();
        final Connector c2 = (Connector) f2.getSource();

        f2.disconnect();

        assertFalse( c2.outcomes().hasNext() );
        assertFalse( c2.externalFlows().hasNext() );
        assertFalse( s1p1.outcomes().hasNext() );

    }
}
