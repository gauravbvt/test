package com.mindalliance.channels.engine.analysis.detectors.collaborationPlan;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * No supervising user for agent with participating user(s).
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/2/13
 * Time: 9:45 PM
 */
public class NoSupervisingUser extends AbstractIssueDetector {

    public NoSupervisingUser() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Agent;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        Agent agent = (Agent) identifiable;
        if ( agent.isSupervisedParticipation() ) {
            ParticipationManager participationManager = communityService.getParticipationManager();
            List<ChannelsUser> users = participationManager.findAllUsersActivelyParticipatingAs( agent, communityService );
            if ( !users.isEmpty() ) {
                for ( Agent supervisorAgent : participationManager.findAllSupervisorsOf( agent, communityService ) ) {
                    if ( participationManager.findAllUsersActivelyParticipatingAs( supervisorAgent, communityService ).isEmpty() ) {
                        for ( ChannelsUser user : users ) {
                            Issue issue = makeIssue( communityService, Issue.COMPLETENESS, agent );
                            issue.setDescription(
                                    user.getFullName()
                                            + " participates as \""
                                            + agent.getName()
                                            + "\" but no one participates as supervisor \""
                                            + supervisorAgent.getName()
                                            + "\""
                            );
                            issue.setSeverity( Level.High );
                            issue.setRemediation( "Have a user participate as \"" + supervisorAgent.getName() + "\"." );
                            issues.add( issue );
                        }
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
        return "User participates without supervisor";
    }
}
