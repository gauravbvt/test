package com.mindalliance.channels.graph;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.pages.Project;

import java.util.Iterator;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 11, 2009
 * Time: 4:20:19 PM
 */
public class TestDefaultDiagramFactory extends AbstractChannelsTest {

    private List<Scenario> scenarios;

    @Override
    protected void setUp() {
        super.setUp();
        scenarios = Project.dqo().list( Scenario.class );

    }

    public void testGetSVG() {
        for ( Scenario scenario : scenarios ) {
            Node selectedNode = findSelected( scenario );
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DiagramFactory diagramFactory = project.getDiagramFactory();
                Diagram flowDiagram = diagramFactory.newFlowMapDiagram( scenario, selectedNode, null, null );
                flowDiagram.render( DiagramFactory.SVG, new BufferedOutputStream( baos ) );
                String svg = baos.toString();
                assertFalse( svg.isEmpty() );
                assertTrue( svg.startsWith( "<?xml" ) );
                // System.out.print( svg );
            } catch ( Exception e ) {
                fail( e.toString() );
            }
        }
    }

    public void testGetPNG() {
        for ( Scenario scenario : scenarios ) {
            Node selectedNode = findSelected( scenario );
            try {
                FileOutputStream fileOut = new FileOutputStream( "target/" + scenario.getName() + ".png" );
                DiagramFactory diagramFactory = project.getDiagramFactory();
                Diagram flowDiagram = diagramFactory.newFlowMapDiagram( scenario, selectedNode, null, null );
                flowDiagram.render( DiagramFactory.PNG, fileOut );
                fileOut.flush();
                fileOut.close();
                assertTrue( new File( "target/" + scenario.getName() + ".png" ).length() > 0 );
            } catch ( IOException e ) {
                fail( e.toString() );
            }
            catch ( DiagramException e ) {
                fail( e.toString() );
            }
        }
    }

    public void testGetImageMap() {
        for ( Scenario scenario : scenarios ) {
            try {
                DiagramFactory diagramFactory = project.getDiagramFactory();
                Diagram flowDiagram = diagramFactory.newFlowMapDiagram( scenario, scenario.getDefaultPart(), null, null );
                String map = flowDiagram.makeImageMap();
                System.out.print( map );
                assertFalse( map.isEmpty() );
                assertTrue( map.startsWith( "<map" ) );
            } catch ( Exception e ) {
                fail( e.toString() );
            }
        }
    }

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
