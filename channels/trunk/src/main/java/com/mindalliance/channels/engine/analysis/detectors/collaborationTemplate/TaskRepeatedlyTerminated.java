package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A task is terminated repeatedly.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/5/14
 * Time: 11:32 AM
 */
public class TaskRepeatedlyTerminated extends AbstractIssueDetector {

    public TaskRepeatedlyTerminated() {
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow sharing = (Flow) identifiable;
        if ( sharing.isRepeating() ) {
            Part part = sharing.isTerminatingToTarget()
                    ? (Part) sharing.getTarget()
                    : sharing.isTerminatingToSource()
                    ? (Part) sharing.getSource()
                    : null;
            if ( part != null ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, sharing );
                issue.setDescription( StringUtils.capitalize( sharing.getDescriptiveLabel() )
                        + " repeatedly terminates task \""
                        + part.getTask()
                        + "\"" );
                issue.setRemediation( "Make "
                                + sharing.getDescriptiveLabel()
                                + " non-repetitive"
                                + "\n or do not make it terminate task \""
                                + part.getTask()
                                + "\"."
                );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Flow && ( (Flow) identifiable ).isSharing();
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Flow repeatedly terminates a task";
    }
}
