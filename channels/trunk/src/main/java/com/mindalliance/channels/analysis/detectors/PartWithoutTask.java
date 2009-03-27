package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Issue;

import java.util.List;
import java.util.ArrayList;

/**
 * Detects the issue where a part has no given task.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 2, 2008
 * Time: 12:41:57 PM
 */
public class PartWithoutTask extends AbstractIssueDetector {
    /** {@inheritDoc} */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( part.hasDefaultTask() ) {
            DetectedIssue issue = new DetectedIssue( DetectedIssue.DEFINITION, modelObject, getTestedProperty() );
            issue.setDescription( "The task is missing," );
            issue.setRemediation( "Provide a task." );
            issue.setSeverity( Issue.Level.Minor );
            issues.add( issue );
        }
        return issues;
    }

    /** {@inheritDoc} */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
    }

    /** {@inheritDoc} */
    public String getTestedProperty() {
        return "task";
    }
}
