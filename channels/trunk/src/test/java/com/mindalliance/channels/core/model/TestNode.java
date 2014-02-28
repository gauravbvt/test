package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.dao.ModelDao;
import com.mindalliance.channels.core.dao.ModelDefinitionManager;
import com.mindalliance.channels.core.dao.ModelManagerImpl;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import junit.framework.TestCase;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
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

    private ModelDao modelDao;

    public TestNode() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ModelDefinitionManager modelDefinitionManager = new ModelDefinitionManager(
            new FileSystemResource( new File("target/channel-test-data" ) ), null );
        modelDefinitionManager.afterPropertiesSet();
        ModelManagerImpl planManager = new ModelManagerImpl( modelDefinitionManager );
        planManager.assignModels();

        CollaborationModel collaborationModel = planManager.getModels().get( 0 );
        modelDao = planManager.getDao( collaborationModel );
        ChannelsUser user = new ChannelsUser();
        user.setCollaborationModel( collaborationModel );

        segment = collaborationModel.getDefaultSegment();
        p1 = modelDao.createPart( segment, null );
            p1.setActor( modelDao.findOrCreate( Actor.class, "p1", null ) );
        p2 = modelDao.createPart( segment, null );
            p2.setActor( modelDao.findOrCreate( Actor.class, "p2", null ) );

        f1 = modelDao.createSend( p1 );
                f1.setName( "A" );
        f2 = modelDao.createReceive( p2 );
                f2.setName( "B" );
        f3 = modelDao.connect( p1, p2, "message", null );
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
        Part part = modelDao.createPart( segment, null );
        assertTrue( part.isPart() );
        assertFalse( part.isConnector() );
        Connector c = modelDao.createConnector( segment, null );
        assertFalse( c.isPart() );
        assertTrue( c.isConnector() );
    }
}
