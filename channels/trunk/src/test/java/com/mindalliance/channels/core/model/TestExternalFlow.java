package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.dao.ModelDao;
import com.mindalliance.channels.core.dao.ModelDefinitionManager;
import com.mindalliance.channels.core.dao.ModelManagerImpl;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import junit.framework.TestCase;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

/**
 * ...
 */
@SuppressWarnings( { "HardCodedStringLiteral" } )
public class TestExternalFlow extends TestCase {

    private Segment s1;
    private Segment s2;

    private Part s1p1;
    private Part s1p2;

    private ModelDao modelDao;

    public TestExternalFlow() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ModelDefinitionManager modelDefinitionManager = new ModelDefinitionManager(
            new FileSystemResource( new File( "target/channel-test-data" ) ), null );
        modelDefinitionManager.afterPropertiesSet();
        ModelManagerImpl planManager = new ModelManagerImpl( modelDefinitionManager );
        planManager.assignModels();

        assertEquals( 1, planManager.getModelDefinitionManager().getPlanNames().size() );
        CollaborationModel collaborationModel = planManager.getModels().get( 0 );

        modelDao = planManager.getDao( collaborationModel );
        ChannelsUser user = new ChannelsUser();
        user.setCollaborationModel( modelDao.getCollaborationModel() );

        s1 = modelDao.createSegment( null, null );
        s1p1 = s1.getDefaultPart();
        s1p1.setActor( new Actor( "p1" ) );
        s1p2 = modelDao.createPart( s1, null );
        s1p2.setActor( new Actor( "p2" ) );

        s2 = modelDao.createSegment( null, null );

        // S2 "included" in S1
        Part s2Part = s2.getDefaultPart();
        s2Part.setActor( new Actor( "p3" ) );
        modelDao.createSend( s2Part );
        modelDao.createReceive( s2Part );

        modelDao.connect( s1p1, s2.inputs().next(), "", null );
        modelDao.connect( s2.outputs().next(), s1p2, "", null );
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
        assertEquals( "p1 notify p3 of \"something\"", f1.toString() );
        assertTrue( s2.inputs().next().externalFlows().hasNext() );

        Flow f2 = s1p2.receives().next();
        assertFalse( f2.isInternal() );
        assertEquals( "p3 notify p2 of \"something\"", f2.toString() );
        assertTrue( s2.outputs().next().externalFlows().hasNext() );
    }

    /**
     * Remove an external flow.
     */
    public void testRemove1() {
        // output
        Flow f = s1p1.sends().next();
        modelDao.disconnect( f );

        assertNull( f.getSource() );
        assertNull( f.getTarget() );
        assertFalse( s1p1.sends().hasNext() );
        assertFalse( s2.inputs().next().externalFlows().hasNext() );

        // input
        Flow f2 = s1p2.receives().next();
        modelDao.disconnect( f2 );

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

        modelDao.disconnect( f1 );

        assertFalse( c1.receives().hasNext() );
        assertFalse( c1.externalFlows().hasNext() );
        assertFalse( s1p2.receives().hasNext() );

        Flow f2 = p3.receives().next();
        Connector c2 = (Connector) f2.getSource();

        modelDao.disconnect( f2 );

        assertFalse( c2.sends().hasNext() );
        assertFalse( c2.externalFlows().hasNext() );
        assertFalse( s1p1.sends().hasNext() );

    }
}
