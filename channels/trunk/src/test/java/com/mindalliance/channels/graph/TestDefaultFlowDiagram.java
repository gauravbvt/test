package com.mindalliance.channels.graph;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;

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
public class TestDefaultFlowDiagram extends AbstractChannelsTest {

    Scenario scenario;

    @Override
    protected void setUp() {
        super.setUp();
        scenario = project.getScenarioDao().getDefaultScenario();
    }

    public void testGetSVG() {
        Node selectedNode = findSelected( scenario );
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            project.getFlowDiagram().getSVG( scenario, selectedNode, project.getScenarioAnalyst(), new BufferedOutputStream( baos ) );
            String svg = baos.toString();
            assertFalse( svg.isEmpty() );
            assertTrue( svg.startsWith( "<?xml" ) );
            // System.out.print( svg );
        } catch ( Exception e ) {
            fail( e.toString() );
        }
    }

    public void testGetPNG() {
        Node selectedNode = findSelected( scenario );
        try {
            FileOutputStream fileOut = new FileOutputStream( "target/test.png" );
            project.getFlowDiagram().getPNG( scenario, selectedNode, project.getScenarioAnalyst(), fileOut );
            fileOut.flush();
            fileOut.close();
            assertTrue( new File( "target/test.png" ).length() > 0 );
        } catch ( IOException e ) {
            fail( e.toString() );
        }
        catch ( DiagramException e ) {
            fail( e.toString() );
        }
    }

     public void testGetImageMap() {
        Scenario scenario = project.getScenarioDao().getDefaultScenario();
        try {
            String map = project.getFlowDiagram().getImageMap(scenario, project.getScenarioAnalyst());
            // System.out.print(map);
            assertFalse(map.isEmpty());
            assertTrue(map.startsWith("<map"));
        } catch (Exception e) {
            fail(e.toString());
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
