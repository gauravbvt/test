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
import java.util.List;

/**
 * Accepted but unconfirmed user participation requests.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/13
 * Time: 10:26 AM
 */
public class UnconfirmedParticipation extends AbstractIssueDetector {

    public UnconfirmedParticipation() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Agent;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>(  );
        Agent agent = (Agent) identifiable;
        ParticipationManager participationManager = communityService.getParticipationManager();
        for ( UserParticipation userParticipation : participationManager.getParticipationsAsAgent( agent, communityService ) ) {
            if ( userParticipation.isAccepted()  && userParticipation.isSupervised( communityService ) ) {
                for ( ChannelsUser user
                        : participationManager.findAllUsersRequestedToConfirm( userParticipation, communityService ) ) {
                    Issue issue = makeIssue( communityService, Issue.COMPLETENESS, agent );
                    issue.setDescription(
                            user.getFullName()
                            + " has not yet confirmed the participation of "
                            + userParticipation.getParticipantFullName( communityService )
                            + " as \""
                            + userParticipation.getAgent( communityService ).getName()
                            + "\""
                    );
                    issue.setSeverity( Level.High ); // todo - set to max failure impact of assigned tasks
                    issue.setRemediation(
                            "Remind "
                                    + user.getFullName()
                                    + " of the participation confirmation request\n"
                                    + "or withdraw the participation request."
                    );
                    issues.add( issue );
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
        return "User participation awaits confirmation";
    }
}
