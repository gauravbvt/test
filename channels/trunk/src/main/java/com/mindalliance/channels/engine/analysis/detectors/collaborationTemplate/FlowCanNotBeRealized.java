package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/7/14
 * Time: 7:03 PM
 */
public class FlowCanNotBeRealized extends AbstractIssueDetector {

    public FlowCanNotBeRealized() {
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        Flow flow = (Flow) identifiable;
        List<Issue> issues = new ArrayList<Issue>();
        if ( flow.isSharing() ) {
            List<String> causes = getAnalyst().findConceptualCausesInPlan( communityService, flow );
            if ( !causes.isEmpty() ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, flow );
                issue.setDescription( "The flow could not be realized because " + ChannelsUtils.listToString( causes, ", and " ) + "." );
                List<String> remediations = getAnalyst().findConceptualRemediationsInPlan( communityService, flow );
                issue.setRemediation( "To make this flow realizable, " + ChannelsUtils.listToString( remediations, ", or " ) );
                issue.setSeverity( computeSharingFailureSeverity( communityService.getModelService(), flow ) );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Flow;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Flow can not be realized";
    }
}
