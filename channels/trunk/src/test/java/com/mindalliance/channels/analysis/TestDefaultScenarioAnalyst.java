package com.mindalliance.channels.analysis;

import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.mindalliance.channels.analysis.detectors.FlowWithoutChannel;
import com.mindalliance.channels.analysis.detectors.FlowWithUndefinedSource;
import com.mindalliance.channels.analysis.detectors.FlowWithUndefinedTarget;
import com.mindalliance.channels.analysis.detectors.UnnamedFlow;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.dao.FireScenario;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 2:23:18 PM
 */
public class TestDefaultScenarioAnalyst extends TestCase {

    /**
     * Model analyst
     */
    private DefaultScenarioAnalyst analyst;
    /**
     * Scenario
     */
    private Scenario scenario;

    public TestDefaultScenarioAnalyst() {
    }

    /**
     * Setup
     */
    protected void setUp() {
        // Initialize analyst
        analyst = new DefaultScenarioAnalyst();
        List<IssueDetector> detectors = new ArrayList<IssueDetector>();
        detectors.add( new FlowWithoutChannel() );
        detectors.add( new FlowWithUndefinedSource() );
        detectors.add( new FlowWithUndefinedTarget() );
        detectors.add( new UnnamedFlow() );
        analyst.setIssueDetectors( detectors );
        // get a scenario
        scenario = new FireScenario();
    }

    /**
     * Apply all applicable detectors to all nodes and flows and gather issues.
     */
    public void testFindAllIssues() {
        List<Issue> allIssues = new ArrayList<Issue>();
        Iterator<Node> nodes = scenario.nodes();
        while ( nodes.hasNext() ) {
            Node node = nodes.next();
            collectIssues( node, allIssues );
            Iterator<Flow> requirements = node.requirements();
            while ( requirements.hasNext() ) {
                Flow requirement = requirements.next();
                collectIssues( requirement, allIssues );
            }
            Iterator<Flow> outcomes = node.outcomes();
            while ( outcomes.hasNext() ) {
                Flow outcome = outcomes.next();
                collectIssues( outcome, allIssues );
            }
        }
        assertFalse( allIssues.isEmpty() );
    }

    private void collectIssues( ModelObject modelObject, List<Issue> collector ) {
        Iterator<Issue> issues = analyst.findIssues( modelObject );
        while ( issues.hasNext() ) {
            Issue issue = issues.next();
            System.out.println( issue );
            collector.add( issue );
        }
    }

    /**
     * Get issue summaries on all model objects.
     */
    public void testGetIssueSummaries() {
        Iterator<Node> nodes = scenario.nodes();
        while ( nodes.hasNext() ) {
            Node node = nodes.next();
            processSummary(node);
            Iterator<Flow> requirements = node.requirements();
            while ( requirements.hasNext() ) {
                processSummary(requirements.next());
            }
            Iterator<Flow> outcomes = node.outcomes();
            while ( outcomes.hasNext() ) {
                processSummary(outcomes.next());
            }
        }
        assertTrue( true );
    }

    private void processSummary(ModelObject modelObject) {
        String summary = analyst.getIssuesSummary( modelObject );
        System.out.println( modelObject.getClass().getSimpleName() +
                " " + modelObject.getName() + " " + modelObject.getId() );
        System.out.println( summary );

    }


}
