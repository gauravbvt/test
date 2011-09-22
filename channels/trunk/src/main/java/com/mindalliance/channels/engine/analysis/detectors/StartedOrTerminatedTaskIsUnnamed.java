package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.engine.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects the issue where a part has no given task and yet is started.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 2, 2008
 * Time: 12:41:57 PM
 */
public class StartedOrTerminatedTaskIsUnnamed extends AbstractIssueDetector {
    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( part.hasDefaultTask() ) {
            if ( part.isStartsWithSegment() || part.isTriggered() ) {
                DetectedIssue issue = makeIssue( queryService,
                                                 DetectedIssue.VALIDITY, modelObject, getTestedProperty() );
                issue.setDescription( "The task is started during the plan segment but is unnamed." );
                issue.setRemediation( "Name the task." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
            if ( part.isTerminatesEventPhase() ) {
                DetectedIssue issue = makeIssue( queryService,
                                                 DetectedIssue.VALIDITY, modelObject, getTestedProperty() );
                issue.setDescription( "The task can terminate the plan segment but is unnamed." );
                issue.setRemediation( "Name the task." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
            if ( part.isTerminated() ) {
                DetectedIssue issue = makeIssue( queryService,
                                                 DetectedIssue.VALIDITY, modelObject, getTestedProperty() );
                issue.setDescription( "The task is terminated during plan segment but is unnamed." );
                issue.setRemediation( "Name the task." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
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
        return "task";
    }

    /**
     * {@inheritDoc}
     */
    protected String getKindLabel() {
        return "Unnamed task started or terminated";
    }
}
