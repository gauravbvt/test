package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

import java.util.ArrayList;
import java.util.List;

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
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Task repeats too soon";
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
                   DetectedIssue issue = makeIssue( DetectedIssue.VALIDITY, modelObject );
                    issue.setDescription( "The task repeats before it usually completes." );
                    issue.setRemediation( "Make the task complete sooner\nor wait longer before it repeats." );
                    issue.setSeverity( Level.Low );
                    issues.add( issue );
                }
            }
            else {
                DetectedIssue issue = makeIssue( DetectedIssue.VALIDITY, modelObject );
                 issue.setDescription( "The task repeats but may not complete beforehand." );
                 issue.setRemediation( "Make the task non-repeating\nor set a completion time for the task." );
                 issue.setSeverity( Level.Low );
                 issues.add( issue );
            }
        }
        return issues;
    }
}
