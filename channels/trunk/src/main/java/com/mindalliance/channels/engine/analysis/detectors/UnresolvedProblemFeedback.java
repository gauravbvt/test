package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * A user submitted a problem feedback that has not yet been resolved.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/13/13
 * Time: 2:19 PM
 */
public class UnresolvedProblemFeedback extends AbstractIssueDetector {

    public UnresolvedProblemFeedback() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof ChannelsUser;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        ChannelsUser user = (ChannelsUser) identifiable;
        List<Feedback> feedbackList = communityService.getFeedbackService().selectInitialFeedbacks(
                communityService,
                false, // not necessarily urgent
                true, // only unresolved
                false, // replied or not
                null, // any topic
                null, // containing anything
                user.getUsername()
        );
        for ( Feedback feedback : feedbackList ) {
            Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, user );
            issue.setDescription( user.getFullName()
                    + " reported "
                    + ( feedback.isUrgent() ? "an urgent" : "a" )
                    + " problem about "
                    + feedback.getTopic().toLowerCase()
                    + " on "
                    + getDateFormat().format( feedback.getCreated() )
                    + " that has not been resolved" );
            issue.setRemediation( "Resolve the problem reported by the participant." );
            issue.setSeverity( feedback.isUrgent() ? Level.High : Level.Medium );
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
        return "Feedback about a problem is not resolved";
    }
}
