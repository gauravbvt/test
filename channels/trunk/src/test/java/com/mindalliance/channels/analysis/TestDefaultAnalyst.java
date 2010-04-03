package com.mindalliance.channels.analysis;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Segment;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

    private Iterator<Segment> segments;

    @Autowired
    private Analyst analyst;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        segments = queryService.list( Segment.class ).iterator();
    }

    /**
     * Apply all applicable detectors to all nodes and flows and gather issues.
     */
    @Test
    public void testFindAllIssues() {
        while ( segments.hasNext() ) {
            Segment segment = segments.next();
            List<Issue> allIssues = new ArrayList<Issue>();
            collectIssues( segment, allIssues );
            Iterator<Node> nodes = segment.nodes();
            while ( nodes.hasNext() ) {
                Node node = nodes.next();
                collectIssues( node, allIssues );
                Iterator<Flow> receives = node.receives();
                while ( receives.hasNext() ) {
                    Flow receive = receives.next();
                    collectIssues( receive, allIssues );
                }
                Iterator<Flow> sends = node.sends();
                while ( sends.hasNext() ) {
                    Flow send = sends.next();
                    collectIssues( send, allIssues );
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
            //System.out.println( issue );
            collector.add( issue );
        }
    }

    /**
     * Get issue summaries on all model objects.
     */
    @Test
    public void testGetIssueSummaries() {
        while ( segments.hasNext() ) {
            Segment segment = segments.next();
            processSummary( segment );
            Iterator<Node> nodes = segment.nodes();
            while ( nodes.hasNext() ) {
                Node node = nodes.next();
                processSummary( node );
                Iterator<Flow> receives = node.receives();
                while ( receives.hasNext() ) {
                    processSummary( receives.next() );
                }
                Iterator<Flow> sends = node.sends();
                while ( sends.hasNext() ) {
                    processSummary( sends.next() );
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
