package com.mindalliance.channels;

import junit.framework.TestCase;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.graph.DefaultGraphBuilder;
import com.mindalliance.channels.graph.GraphvizRenderer;
import com.mindalliance.channels.graph.DefaultFlowDiagram;
import com.mindalliance.channels.analysis.DefaultScenarioAnalyst;
import com.mindalliance.channels.analysis.IssueDetector;
import com.mindalliance.channels.analysis.detectors.FlowWithoutChannel;
import com.mindalliance.channels.analysis.detectors.FlowWithUndefinedSource;
import com.mindalliance.channels.analysis.detectors.FlowWithUndefinedTarget;
import com.mindalliance.channels.analysis.detectors.UnnamedFlow;
import com.mindalliance.channels.analysis.detectors.PartWithoutTask;
import com.mindalliance.channels.analysis.detectors.PartWithoutRole;
import com.mindalliance.channels.analysis.detectors.SinglePointOfFailure;
import com.mindalliance.channels.analysis.detectors.PotentialDeadlock;

import java.util.List;
import java.util.ArrayList;

import org.apache.wicket.util.tester.WicketTester;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 3, 2008
 * Time: 8:24:56 PM
 */
public class AbstractChannelsTest extends TestCase {

    protected Project project;
    protected WicketTester tester;

    @Override
    protected void setUp() {
        project = new Project();
        // Set DAO
        Dao dao = new Memory();
        project.setDao( dao );
        project.setGraphBuilder( new DefaultGraphBuilder() );
        // Set default scenario
        // project.getScenarioDao().addScenario(new FireScenario());
        // Set flow diagram
        GraphvizRenderer<Node, Flow> graphRenderer = new GraphvizRenderer<Node, Flow>();
        graphRenderer.setDotPath( "/usr/bin" );
        graphRenderer.setAlgo( "dot" );
        graphRenderer.setTimeout( 5000 );
        DefaultFlowDiagram flowDiagram = new DefaultFlowDiagram();
        flowDiagram.setGraphRenderer( graphRenderer );
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
        detectors.add( new SinglePointOfFailure() );
        detectors.add( new PotentialDeadlock() );
        analyst.setIssueDetectors( detectors );
        project.setScenarioAnalyst( analyst );
        tester = new WicketTester( project );
    }

    public void testNothing() {
        assertTrue( true );
    }

}
