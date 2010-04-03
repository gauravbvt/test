package com.mindalliance.channels.model;

import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.SimpleIdGenerator;
import com.mindalliance.channels.export.DummyExporter;
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
    private Segment segment;

    private PlanDao planDao;

    public TestNode() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PlanManager planManager = new PlanManager( new DummyExporter(), new SimpleIdGenerator() );
        planManager.validate();

        Plan plan = planManager.getPlans().get( 0 );
        planDao = planManager.getDao( plan );
        User user = new User();
        user.setPlan( plan );

        segment = planDao.createSegment( null, null );
        p1 = planDao.createPart( segment, null );
            p1.setActor( planDao.findOrCreate( Actor.class, "p1", null ) );
        p2 = planDao.createPart( segment, null );
            p2.setActor( planDao.findOrCreate( Actor.class, "p2", null ) );

        f1 = p1.createSend( planDao );
                f1.setName( "A" );
        f2 = p2.createReceive( planDao );
                f2.setName( "B" );
        f3 = planDao.connect( p1, p2, "message", null );
    }

    public void testSetSends() {
        p1.setSends( new HashMap<Long,Flow>() );
        assertFalse( p1.sends().hasNext() );

        // setSends does not disconnect anymore ?
        assertNull( f1.getSource() );
        assertNull( f3.getSource() );

        Map<Long,Flow> flows = new HashMap<Long,Flow>();
        flows.put( f1.getId(), f1 );
        flows.put( f2.getId(), f2 );
        p1.setSends( flows );
        assertSame( f1, p1.getFlow( f1.getId() ) );
        assertSame( p1, f1.getSource() );
        assertSame( p1, f2.getSource() );
        assertSame( f2, p1.getFlow( f2.getId() ) );
        assertNull( p1.getFlow( f3.getId() ) );
    }

    public void testSetReceives() {
        p2.setReceives( new HashMap<Long,Flow>() );
        assertFalse( p2.receives().hasNext() );

        // Set receives does not disconnect anymore?
        assertNull( f2.getTarget() );
        assertNull( f3.getTarget() );

        Map<Long, Flow> flows = new HashMap<Long,Flow>();
        flows.put( f1.getId(), f1 );
        flows.put( f2.getId(), f2 );
        p2.setReceives( flows );
        assertSame( f1, p2.getFlow( f1.getId() ) );
        assertSame( p2, f1.getTarget() );
        assertSame( p2, f2.getTarget() );
        assertSame( f2, p2.getFlow( f2.getId() ) );
        assertNull( p2.getFlow( f3.getId() ) );
    }

    public void testSends() {
        Iterator<Flow> iterator1 = p1.sends();
        assertTrue( iterator1.hasNext() );
        iterator1.next();
        iterator1.next();
        assertFalse( iterator1.hasNext() );

        assertFalse( p2.sends().hasNext() );
    }

    public void testReceives() {
        Iterator<Flow> iterator1 = p2.receives();
        assertTrue( iterator1.hasNext() );
        iterator1.next();
        iterator1.next();
        assertFalse( iterator1.hasNext() );

        assertFalse( p1.receives().hasNext() );
    }

    public void testIsness() {
        Part part = planDao.createPart( segment, null );
        assertTrue( part.isPart() );
        assertFalse( part.isConnector() );
        Connector c = planDao.createConnector( segment, null );
        assertFalse( c.isPart() );
        assertTrue( c.isConnector() );
    }
}
