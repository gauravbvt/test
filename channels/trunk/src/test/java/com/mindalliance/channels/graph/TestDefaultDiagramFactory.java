package com.mindalliance.channels.graph;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Segment;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 11, 2009
 * Time: 4:20:19 PM
 */
public class TestDefaultDiagramFactory extends AbstractChannelsTest {

    private List<Segment> segments;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        segments = queryService.list( Segment.class );
    }

    @Test
    public void testGetPNG() {
        for ( Segment segment : segments ) {
            Node selectedNode = findSelected( segment );
            try {
                FileOutputStream fileOut = new FileOutputStream( "target/" + segment.getName() + ".png" );
                DiagramFactory diagramFactory = wicketApplication.getDiagramFactory();
                Diagram flowDiagram = diagramFactory.newFlowMapDiagram( segment, selectedNode, null, null );
                flowDiagram.render(
                        "test",
                        DiagramFactory.PNG,
                        fileOut,
                        getAnalyst(),
                        wicketApplication.getDiagramFactory() );
                fileOut.flush();
                fileOut.close();
                assertTrue( new File( "target/" + segment.getName() + ".png" ).length() > 0 );
            } catch ( IOException e ) {
                fail( e.toString() );
            }
            catch ( DiagramException e ) {
                fail( e.toString() );
            }
        }
    }

    @Test
    public void testGetImageMap() {
        for ( Segment segment : segments ) {
            DiagramFactory diagramFactory = wicketApplication.getDiagramFactory();
            Diagram flowDiagram = diagramFactory.newFlowMapDiagram( segment, segment.getDefaultPart(), null, null );
            String map = flowDiagram.makeImageMap( "test", getAnalyst(), wicketApplication.getDiagramFactory() );
            System.out.print( map );
            assertFalse( map.isEmpty() );
            assertTrue( map.startsWith( "<map" ) );
        }
    }

    private Node findSelected( Segment segment ) {
        Node selectedNode = null;
        Iterator<Node> nodes = segment.nodes();
        if ( nodes.hasNext() ) {
            selectedNode = nodes.next();
            while ( !selectedNode.isPart() || nodes.hasNext() ) {
                selectedNode = nodes.next();
            }
        }
        return selectedNode;
    }

}
