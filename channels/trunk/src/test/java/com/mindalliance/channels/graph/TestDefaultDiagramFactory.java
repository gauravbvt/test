package com.mindalliance.channels.graph;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.DiagramFactory;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Segment;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

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
    public void testGetSVG() {
        for ( Segment segment : segments ) {
            Node selectedNode = findSelected( segment );
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DiagramFactory diagramFactory = wicketApplication.getDiagramFactory();
            Diagram flowDiagram = diagramFactory.newFlowMapDiagram( segment, selectedNode, null, null );
            flowDiagram.render( DiagramFactory.SVG, new BufferedOutputStream( baos ) );
            String svg = baos.toString();
            assertFalse( svg.isEmpty() );
            assertTrue( svg.startsWith( "<?xml" ) );
            // System.out.print( svg );
        }
    }

    @Test
    public void testGetPNG() {
        for ( Segment segment : segments ) {
            Node selectedNode = findSelected( segment );
            try {
                FileOutputStream fileOut = new FileOutputStream( "target/" + segment.getName() + ".png" );
                DiagramFactory diagramFactory = wicketApplication.getDiagramFactory();
                Diagram flowDiagram = diagramFactory.newFlowMapDiagram( segment, selectedNode, null, null );
                flowDiagram.render( DiagramFactory.PNG, fileOut );
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
            String map = flowDiagram.makeImageMap();
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
