/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects the issue where a part has no given task and yet is started.
 */
public class StartedOrTerminatedTaskIsUnnamed extends AbstractIssueDetector {
    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        if ( part.hasDefaultTask() ) {
            if ( part.isAutoStarted() || part.isTriggered() ) {
                DetectedIssue issue = makeIssue( communityService,
                                                 DetectedIssue.VALIDITY, modelObject, getTestedProperty() );
                issue.setDescription( "The task is started during the segment but is unnamed." );
                issue.setRemediation( "Name the task." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
            if ( part.isTerminatesEventPhase() ) {
                DetectedIssue issue = makeIssue( communityService,
                                                 DetectedIssue.VALIDITY, modelObject, getTestedProperty() );
                issue.setDescription( "The task can terminate the segment but is unnamed." );
                issue.setRemediation( "Name the task." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
            if ( part.isTerminated() ) {
                DetectedIssue issue = makeIssue( communityService,
                                                 DetectedIssue.VALIDITY, modelObject, getTestedProperty() );
                issue.setDescription( "The task is terminated during segment but is unnamed." );
                issue.setRemediation( "Name the task." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Part;
    }

    @Override
    public String getTestedProperty() {
        return "task";
    }

    @Override
    protected String getKindLabel() {
        return "Task started or terminated has no name";
    }
}
