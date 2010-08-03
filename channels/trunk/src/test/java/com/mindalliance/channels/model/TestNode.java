package com.mindalliance.channels.model;

import com.mindalliance.channels.dao.DefinitionManager;
import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import junit.framework.TestCase;

import java.util.Iterator;

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
        DefinitionManager definitionManager = new DefinitionManager();
        definitionManager.afterPropertiesSet();
        PlanManager planManager = new PlanManager( definitionManager );
        planManager.assignPlans();

        Plan plan = planManager.getPlans().get( 0 );
        planDao = planManager.getDao( plan );
        User user = new User();
        user.setPlan( plan );

        segment = plan.getDefaultSegment();
        p1 = planDao.createPart( segment, null );
            p1.setActor( planDao.findOrCreate( Actor.class, "p1", null ) );
        p2 = planDao.createPart( segment, null );
            p2.setActor( planDao.findOrCreate( Actor.class, "p2", null ) );

        f1 = planDao.createSend( p1 );
                f1.setName( "A" );
        f2 = planDao.createReceive( p2 );
                f2.setName( "B" );
        f3 = planDao.connect( p1, p2, "message", null );
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
