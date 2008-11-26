package com.mindalliance.channels.graph;

import com.mindalliance.channels.dao.FireScenario;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Scenario;
import junit.framework.TestCase;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 21, 2008
 * Time: 8:57:25 AM
 */
public class TestDefaultFlowDiagram extends TestCase {

    private FlowDiagram<Node, Flow> flowDiagram;

    @Override
    protected void setUp() {
        GraphvizRenderer<Node, Flow> graphRenderer = new GraphvizRenderer<Node, Flow>();
        graphRenderer.setDotPath( "/usr/bin" );
        graphRenderer.setAlgo( "neato" );
        graphRenderer.setTimeout( 5000 );
        flowDiagram = new DefaultFlowDiagram();
        flowDiagram.setGraphRenderer( graphRenderer );
        flowDiagram.setGraphBuilder( new DefaultGraphBuilder() );
        flowDiagram.setImageDirectory( "src/site/resources/images" );
    }

    public void testGetSVG() {
        Scenario scenario = new FireScenario();
        Node selectedNode = findSelected( scenario );
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            flowDiagram.getSVG( scenario, selectedNode, new BufferedOutputStream( baos ) );
            String svg = baos.toString();
            assertFalse( svg.isEmpty() );
            assertTrue( svg.startsWith( "<?xml" ) );
            // System.out.print( svg );
        } catch ( Exception e ) {
            fail( e.toString() );
        }
    }

    public void testGetPNG() {
        Scenario scenario = new FireScenario();
        Node selectedNode = findSelected( scenario );
        try {
            FileOutputStream fileOut = new FileOutputStream( "test.png" );
            flowDiagram.getPNG( scenario, selectedNode, fileOut );
            fileOut.flush();
            fileOut.close();
            assertTrue( new File( "test.png" ).length() > 0 );
        } catch ( IOException e ) {
            fail( e.toString() );
        }
        catch ( DiagramException e ) {
            fail( e.toString() );
        }
    }

    /* public void testGetImageMap() {
        Scenario scenario = new FireScenario();
        Node node = scenario.nodes().next();
        try {
            ImageMap imageMap = defaultFlowDiagram.getImageMap(scenario, node);
            MarkupStream stream = imageMap.getMarkupStream();
            String map = stream.toString();
            assertFalse(map.isEmpty());
            assertTrue(map.startsWith("<"));
            System.out.print(map);
        } catch (Exception e) {
            fail(e.toString());
        }
    }*/

    private Node findSelected( Scenario scenario ) {
        Node selectedNode = null;
        Iterator<Node> nodes = scenario.nodes();
        if ( nodes.hasNext() ) {
            selectedNode = nodes.next();
            while ( !selectedNode.isPart() || nodes.hasNext() ) {
                selectedNode = nodes.next();
            }
        }
        return selectedNode;
    }

}
