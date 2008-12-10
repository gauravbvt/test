package com.mindalliance.channels.analysis;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 2:23:18 PM
 */
public class TestDefaultScenarioAnalyst extends AbstractChannelsTest {

    Scenario scenario;
    ScenarioAnalyst analyst;

    @Override
    protected void setUp() {
        super.setUp();
        scenario = project.getDao().getDefaultScenario();
        analyst = project.getScenarioAnalyst();
    }

    /**
     * Apply all applicable detectors to all nodes and flows and gather issues.
     */
    public void testFindAllIssues() {
        List<Issue> allIssues = new ArrayList<Issue>();
        collectIssues( scenario, allIssues );
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
        processSummary( scenario );
        Iterator<Node> nodes = scenario.nodes();
        while ( nodes.hasNext() ) {
            Node node = nodes.next();
            processSummary( node );
            Iterator<Flow> requirements = node.requirements();
            while ( requirements.hasNext() ) {
                processSummary( requirements.next() );
            }
            Iterator<Flow> outcomes = node.outcomes();
            while ( outcomes.hasNext() ) {
                processSummary( outcomes.next() );
            }
        }
        assertTrue( true );
    }

    private void processSummary( ModelObject modelObject ) {
        String summary = analyst.getIssuesSummary( modelObject );
        System.out.println();
        System.out.println( modelObject.getClass().getSimpleName() +
                " " + modelObject.getName() + " " + modelObject.getId() );
        System.out.println( "-----------------" );
        System.out.println( summary );

    }


}
