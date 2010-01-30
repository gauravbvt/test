package com.mindalliance.channels.analysis;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Segment;

import java.io.IOException;
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
public class TestDefaultAnalyst extends AbstractChannelsTest {

    Iterator<Segment> segments;
    Analyst analyst;

    @Override
    protected void setUp() throws IOException {
        super.setUp();
        segments = app.getQueryService().list( Segment.class ).iterator();
        analyst = app.getAnalyst();
    }

    /**
     * Apply all applicable detectors to all nodes and flows and gather issues.
     */
    public void testFindAllIssues() {
        while ( segments.hasNext() ) {
            Segment segment = segments.next();
            List<Issue> allIssues = new ArrayList<Issue>();
            collectIssues( segment, allIssues );
            Iterator<Node> nodes = segment.nodes();
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
    }

    private void collectIssues( ModelObject modelObject, List<Issue> collector ) {
        Iterator<Issue> issues = analyst.listIssues( modelObject,
                Analyst.INCLUDE_PROPERTY_SPECIFIC ).iterator();
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
        while ( segments.hasNext() ) {
            Segment segment = segments.next();
            processSummary( segment );
            Iterator<Node> nodes = segment.nodes();
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
    }

    private void processSummary( ModelObject modelObject ) {
        String summary = analyst.getIssuesSummary( modelObject,
                Analyst.INCLUDE_PROPERTY_SPECIFIC );
        System.out.println();
        System.out.println( modelObject.getClass().getSimpleName() +
                " " + modelObject.getName() + " " + modelObject.getId() );
        System.out.println( "-----------------" );
        System.out.println( summary );

    }


}
