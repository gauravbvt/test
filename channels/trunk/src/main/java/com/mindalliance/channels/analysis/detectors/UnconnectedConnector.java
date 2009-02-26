package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;

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

    public UnconnectedConnector() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
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
    protected List<Issue> doDetectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        Iterator<Flow> outcomes = part.outcomes();
        while ( outcomes.hasNext() ) {
            Flow outcome = outcomes.next();
            if ( outcome.getTarget().isConnector() && !part.hasMultipleOutcomes( outcome.getName() ) ) {
                DetectedIssue issue = new DetectedIssue( DetectedIssue.STRUCTURAL, part );
                issue.setDescription( "'" + outcome.getName() + "' is produced but never sent." );
                issue.setSeverity( Issue.Level.Minor );
                issues.add( issue );
            }
        }
        Iterator<Flow> requirements = part.requirements();
        while ( requirements.hasNext() ) {
            Flow requirement = requirements.next();
            if ( requirement.getSource().isConnector() && !part.hasMultipleRequirements( requirement.getName() ) ) {
                DetectedIssue issue = new DetectedIssue( DetectedIssue.STRUCTURAL, part );
                issue.setDescription(
                                ( requirement.isRequired() ? "Required " : "" )
                                + "'"
                                + requirement.getName()
                                + "' is needed but never received." );
                issue.setSeverity( requirement.isRequired() ? Issue.Level.Major : Issue.Level.Minor );
                issues.add( issue );
            }
        }
        return issues;
    }
}
