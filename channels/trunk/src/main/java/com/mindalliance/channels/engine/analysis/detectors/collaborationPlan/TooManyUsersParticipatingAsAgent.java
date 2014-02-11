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
 * Too many users participating as an agent.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/8/13
 * Time: 2:13 PM
 */
public class TooManyUsersParticipatingAsAgent extends AbstractIssueDetector {

    public TooManyUsersParticipatingAsAgent() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Agent;
    }

    @Override
    public String getKindLabel() {
        return "Too many users assigned to position";
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        Agent agent = (Agent) identifiable;
        int maxParticipation = agent.getMaxParticipation();
        if ( maxParticipation != -1 ) {
            int count = communityService.getParticipationManager().findUsersActivelyParticipatingAs( agent, communityService ).size();
            if ( count > maxParticipation ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, agent );
                issue.setDescription( count + " users participate as " + agent.getName()
                        + "; maximum is " + maxParticipation );
                issue.setRemediation( "Remove a user participation as " + agent.getName()
                        + "\nor allow more users to participate as " + agent.getName() );
                issue.setSeverity( Level.High );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }
}
