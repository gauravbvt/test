package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.Part;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 18, 2009
 * Time: 2:49:13 PM
 */
public class PartWithInvalidTiming extends AbstractIssueDetector {
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
     * Do the work of detecting issues about the model object.
     *
     * @param modelObject -- the model object being analyzed
     * @return -- a list of issues
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( part.isRepeating() ) {
            if ( part.isSelfTerminating() ) {
                if ( part.getCompletionTime().compareTo( part.getRepeatsEvery() ) > 0 ) {
                   DetectedIssue issue = new DetectedIssue( DetectedIssue.DEFINITION, modelObject );
                    issue.setDescription( "The task repeats before it usually completes." );
                    issue.setRemediation( "Make the task complete sooner or wait longer before it repeats." );
                    issue.setSeverity( Issue.Level.Minor );
                    issues.add( issue );
                }
            }
            else {
                DetectedIssue issue = new DetectedIssue( DetectedIssue.DEFINITION, modelObject );
                 issue.setDescription( "The task repeats but may not complete beforehand." );
                 issue.setRemediation( "Make the task non-repeating or set a completion time for the task." );
                 issue.setSeverity( Issue.Level.Minor );
                 issues.add( issue );
            }
        }
        return issues;
    }
}
