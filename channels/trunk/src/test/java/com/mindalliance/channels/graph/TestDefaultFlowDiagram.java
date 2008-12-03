package com.mindalliance.channels.graph;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.DefaultScenarioAnalyst;
import com.mindalliance.channels.analysis.IssueDetector;
import com.mindalliance.channels.analysis.detectors.FlowWithUndefinedSource;
import com.mindalliance.channels.analysis.detectors.FlowWithUndefinedTarget;
import com.mindalliance.channels.analysis.detectors.FlowWithoutChannel;
import com.mindalliance.channels.analysis.detectors.PartWithoutRole;
import com.mindalliance.channels.analysis.detectors.PartWithoutTask;
import com.mindalliance.channels.analysis.detectors.UnnamedFlow;
import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.dao.ScenarioDao;
import com.mindalliance.channels.pages.Project;
import junit.framework.TestCase;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 21, 2008
 * Time: 8:57:25 AM
 */
public class TestDefaultFlowDiagram extends TestCase {

    private Project project;

    @Override
    protected void setUp() {
        project = new Project();
        // Set DAO
        ScenarioDao dao = new Memory();
        project.setScenarioDao( dao );
        // Set default scenario
        // project.getScenarioDao().addScenario(new FireScenario());
        // Set flow diagram
        GraphvizRenderer<Node, Flow> graphRenderer = new GraphvizRenderer<Node, Flow>();
        graphRenderer.setDotPath( "/usr/bin" );
        graphRenderer.setAlgo( "neato" );
        graphRenderer.setTimeout( 5000 );
        FlowDiagram<Node,Flow> flowDiagram = new DefaultFlowDiagram();
        flowDiagram.setGraphRenderer( graphRenderer );
        flowDiagram.setGraphBuilder( new DefaultGraphBuilder() );
        flowDiagram.setImageDirectory( "src/site/resources/images" );
        project.setFlowDiagram( flowDiagram );
        // Set scenario analyst
        // Initialize analyst
        DefaultScenarioAnalyst analyst = new DefaultScenarioAnalyst();
        List<IssueDetector> detectors = new ArrayList<IssueDetector>();
        detectors.add( new FlowWithoutChannel() );
        detectors.add( new FlowWithUndefinedSource() );
        detectors.add( new FlowWithUndefinedTarget() );
        detectors.add( new UnnamedFlow() );
        detectors.add( new PartWithoutTask() );
        detectors.add( new PartWithoutRole() );
        analyst.setIssueDetectors( detectors );
        project.setScenarioAnalyst( analyst );

    }

    public void testGetSVG() {
        Scenario scenario = project.getScenarioDao().getDefaultScenario();
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
        Scenario scenario = project.getScenarioDao().getDefaultScenario();
        Node selectedNode = findSelected( scenario );
        try {
            FileOutputStream fileOut = new FileOutputStream( "test.png" );
            project.getFlowDiagram().getPNG( scenario, selectedNode, project.getScenarioAnalyst(), fileOut );
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

     public void testGetImageMap() {
        Scenario scenario = project.getScenarioDao().getDefaultScenario();
        try {
            String map = project.getFlowDiagram().getImageMap(scenario, project.getScenarioAnalyst());
            System.out.print(map);
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
