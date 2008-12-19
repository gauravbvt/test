package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Node;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.collections.Predicate;

/**
 * Detects whether the scenario has a connector without external flows.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 19, 2008
 * Time: 2:52:42 PM
 */
public class UnconnectedConnector extends AbstractIssueDetector {
    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Scenario;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    protected List<Issue> doDetectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Scenario scenario = (Scenario) modelObject;
        Iterator<Connector> unconnectedConnectors = new FilterIterator( scenario.nodes(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Node) obj ).isConnector() && !((Connector)obj).isConnected();
                    }
                } );
        while ( unconnectedConnectors.hasNext() ) {
            Connector connector = unconnectedConnectors.next();
            String flowName = connector.getInnerFlow().getName();
            Issue issue = new Issue(Issue.STRUCTURAL, scenario);
            issue.setDescription("Connection point -" + (flowName.isEmpty() ? "<no name>" : flowName) + "- is never used.");
            issue.setRemediation("Consider removing it.");
            issues.add(issue);
        }
        return issues;
    }
}
