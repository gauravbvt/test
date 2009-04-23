package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects whether the scenario has unsatisifed needs or unused capabilities.
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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        for ( Flow capability : getDqo().findUnusedCapabilities( part ) ) {
            DetectedIssue issue = new DetectedIssue( DetectedIssue.STRUCTURAL, part );
            issue.setDescription( "'" + capability.getName() + "' is produced but never sent." );
            issue.setSeverity( Issue.Level.Minor );
            issues.add( issue );
        }
        for ( Flow need : getDqo().findUnsatisfiedNeeds( part ) ) {
            DetectedIssue issue = new DetectedIssue( DetectedIssue.STRUCTURAL, part );
            issue.setDescription(
                    ( need.isRequired() ? "Required " : "" )
                            + "'"
                            + need.getName()
                            + "' is needed but never received." );
            issue.setSeverity( need.isRequired() ? Issue.Level.Major : Issue.Level.Minor );
            issues.add( issue );
        }
        return issues;
    }
}
