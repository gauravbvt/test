package com.mindalliance.channels.engine.analysis.detectors.collaborationPlan;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User participates as own supervisor.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/2/13
 * Time: 9:20 PM
 */
public class UserSupervisesSelf extends AbstractIssueDetector {

    public UserSupervisesSelf() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Agent;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Agent agent = (Agent)identifiable;
        ParticipationManager participationManager = communityService.getParticipationManager();
        List<ChannelsUser> users = participationManager.findAllUsersParticipatingAs( agent, communityService );
        for( Agent supervisorAgent : participationManager.findAllSupervisorsOf( agent, communityService ) ) {
            for ( ChannelsUser supervisorUser
                    : participationManager.findAllUsersParticipatingAs( supervisorAgent, communityService ) ) {
                for ( ChannelsUser user : users ) {
                    if ( supervisorUser.equals( user ) ) {
                        Issue issue = makeIssue( communityService, Issue.VALIDITY, agent );
                        issue.setDescription(
                                user.getFullName()
                                + " participates as \""
                                + agent.getName()
                                + "\" but also as its supervisor \""
                                + supervisorAgent.getName()
                                + "\""
                        );
                        issue.setSeverity( Level.Medium );
                        issue.setRemediation(
                                "Terminate the participation of "
                                + user.getFullName()
                                + " as \""
                                + agent.getName()
                                + "\"\nor terminate his/her participation as \""
                                + supervisorAgent.getName()
                                + "\"."
                        );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "User participates as own supervisor";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
