package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects whether the plan segment has unsatisifed needs or unused capabilities.
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
    protected String getLabel() {
        return "Information never communicated";
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        for ( Flow capability : getQueryService().findUnusedCapabilities( part ) ) {
            DetectedIssue issue = makeIssue( DetectedIssue.COMPLETENESS, part );
            issue.setDescription( "'" + capability.getName() + "' is produced but never sent." );
            issue.setRemediation( "Add a commitment to share " + capability.getName() + " produced by the task." );
            issue.setSeverity( Issue.Level.Minor );
            issues.add( issue );
        }
        for ( Flow need : getQueryService().findUnconnectedNeeds( part ) ) {
            DetectedIssue issue = makeIssue( DetectedIssue.COMPLETENESS, part );
            issue.setDescription(
                    ( need.isRequired() ? "Required " : "" )
                            + "'"
                            + need.getName()
                            + "' is needed but never received." );
            issue.setRemediation( "Add a commitment to share " + need.getName() + " needed by the task." );
            issue.setSeverity( need.isRequired() ? Issue.Level.Major : Issue.Level.Minor );
            issues.add( issue );
        }
        return issues;
    }
}
