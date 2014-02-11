package com.mindalliance.channels.engine.analysis.detectors.collaborationPlan;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * No user participating as an actor.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/8/13
 * Time: 1:03 PM
 */
public class MissingUserParticipation extends AbstractIssueDetector {

    public MissingUserParticipation() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Agent;
    }

    @Override
    public String getKindLabel() {
        return "No user assigned to position";
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        final Agent agent = (Agent) identifiable;
        int count = communityService.getParticipationManager().findAllUsersParticipatingAs( agent, communityService ).size();

        if ( count == 0 ) {
            Issue issue = makeIssue( communityService, Issue.COMPLETENESS, agent );
            issue.setDescription( "No user participates as " + agent.getName() );
            issue.setRemediation( "Request that a user participate as " + agent.getName() );
            issue.setSeverity( Level.High );
            issues.add( issue );
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }
}
