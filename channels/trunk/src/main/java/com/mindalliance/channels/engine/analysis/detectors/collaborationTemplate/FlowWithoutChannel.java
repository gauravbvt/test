/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;
import com.mindalliance.channels.core.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects issue where a flow has no defined channel.
 */
public class FlowWithoutChannel extends AbstractIssueDetector {

    public FlowWithoutChannel() {
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();

        Flow flow = (Flow) modelObject;
        if ( flow.isSharing() && !flow.isToSelf() ) {
            // There is no channel in a flow that requires one
            List<Channel> flowChannels = flow.getEffectiveChannels();
            if ( flowChannels.isEmpty() )
                issues.add( createIssue( communityService,
                                         modelObject,
                                         getSeverity( flow, queryService ),
                                         "At least one channel is required.",
                                         "Add a channel" ) );
        }
        return issues;
    }

    private static Level getSeverity( Flow flow, QueryService queryService ) {
        return flow.isSharing() ? computeSharingFailureSeverity( queryService, flow ) : Level.Low;
    }

    private DetectedIssue createIssue( CommunityService communityService, Identifiable modelObject, Level severity,
                                       String description, String remediation ) {
        DetectedIssue issue = makeIssue( communityService, Issue.COMPLETENESS, modelObject );
        issue.setDescription( description );
        issue.setRemediation( remediation );
        issue.setSeverity( severity );
        return issue;
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    protected String getKindLabel() {
        return "Flow without channel";
    }
}
