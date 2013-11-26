package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * A criticial notification is not a triggering receive.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/22/13
 * Time: 10:21 AM
 */
public class CriticalNotificationNotTriggering extends AbstractIssueDetector {

    public CriticalNotificationNotTriggering() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow
                && ((Flow)modelObject).isSharing()
                && ((Flow)modelObject).isNotification();
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>(  );
        Flow flow = (Flow)modelObject;
        if ( flow.isSharing()
                && flow.isNotification()
                && !flow.isTriggeringToTarget()
                && flow.isCritical() ) {
            Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, flow );
            issue.setDescription( "The information \"" + flow.getName()
                    + "\" is critical but it is is a non-triggering notification (no guarantee it will be received when needed by the task)." );
            issue.setRemediation( "Make it a triggering notification " +
                    "\nor make it a request " +
                    "\nor downgrade it to merely useful." );
            issue.setSeverity( queryService.computeSharingPriority( flow ) );
            issues.add( issue );
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Critical notification is not triggering a task";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
