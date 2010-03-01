package com.mindalliance.channels.model;

import com.mindalliance.channels.attachments.BitBucket;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.SimpleIdGenerator;
import com.mindalliance.channels.export.DummyExporter;
import com.mindalliance.channels.query.DefaultQueryService;
import junit.framework.TestCase;

/**
 * ...
 */
@SuppressWarnings( { "HardCodedStringLiteral" } )
public class TestExternalFlow extends TestCase {

    private DefaultQueryService queryService;
    private Segment s1;
    private Segment s2;

    private Part s1p1;
    private Part s1p2;

    public TestExternalFlow() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PlanManager planManager = new PlanManager( new DummyExporter(), new SimpleIdGenerator() );
        planManager.afterPropertiesSet();
        User user = new User();
        user.setPlan( planManager.getPlans().get( 0 ) );
        queryService = new DefaultQueryService( planManager, new BitBucket() );
        queryService.afterPropertiesSet();

        s1 = queryService.createSegment();
        s1p1 = s1.getDefaultPart();
        s1p1.setActor( new Actor( "p1" ) );
        s1p2 = queryService.createPart( s1 );
        s1p2.setActor( new Actor( "p2" ) );

        s2 = queryService.createSegment();

        // S2 "included" in S1
        Part s2Part = s2.getDefaultPart();
        s2Part.setActor( new Actor( "p3" ) );
        s2Part.createSend( queryService );
        s2Part.createReceive( queryService );

        queryService.connect( s1p1, s2.inputs().next(), "" );
        queryService.connect( s2.outputs().next(), s1p2, "" );
    }

    public void testConstructor() {
        // test degenerate cases
        try {
            Part p = s2.getDefaultPart();
            new ExternalFlow( p, p.sends().next().getTarget(), "" );
            fail();
        } catch ( IllegalArgumentException ignored ) {}

        try {
            new ExternalFlow( s1p1, s1p2, "" );
            fail();
        } catch ( IllegalArgumentException ignored ) {}
    }

    public void testInitial() {
        Flow f1 = s1p1.sends().next();
        assertFalse( f1.isInternal() );
        assertEquals( "p1 notify p3 of something", f1.toString() );
        assertTrue( s2.inputs().next().externalFlows().hasNext() );

        Flow f2 = s1p2.receives().next();
        assertFalse( f2.isInternal() );
        assertEquals( "p3 notify p2 of something", f2.toString() );
        assertTrue( s2.outputs().next().externalFlows().hasNext() );
    }

    /**
     * Remove an external flow.
     */
    public void testRemove1() {
        // output
        Flow f = s1p1.sends().next();
        f.disconnect();

        assertNull( f.getSource() );
        assertNull( f.getTarget() );
        assertFalse( s1p1.sends().hasNext() );
        assertFalse( s2.inputs().next().externalFlows().hasNext() );

        // input
        Flow f2 = s1p2.receives().next();
        f2.disconnect();

        assertNull( f2.getSource() );
        assertNull( f2.getTarget() );
        assertFalse( s1p2.receives().hasNext() );
        assertFalse( s2.outputs().next().externalFlows().hasNext() );
    }

    /**
     * Remove a connected connector.
     */
    public void testRemove2() {
        Part p3 = s2.getDefaultPart();
        Flow f1 = p3.sends().next();
        Connector c1 = (Connector) f1.getTarget();

        f1.disconnect();

        assertFalse( c1.receives().hasNext() );
        assertFalse( c1.externalFlows().hasNext() );
        assertFalse( s1p2.receives().hasNext() );

        Flow f2 = p3.receives().next();
        Connector c2 = (Connector) f2.getSource();

        f2.disconnect();

        assertFalse( c2.sends().hasNext() );
        assertFalse( c2.externalFlows().hasNext() );
        assertFalse( s1p1.sends().hasNext() );

    }
}
