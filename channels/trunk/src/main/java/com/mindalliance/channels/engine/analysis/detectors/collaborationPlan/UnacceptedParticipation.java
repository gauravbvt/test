package com.mindalliance.channels.engine.analysis.detectors.collaborationPlan;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User has not yet accepted participation as a given agent.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/13
 * Time: 9:31 AM
 */
public class UnacceptedParticipation extends AbstractIssueDetector {

    public UnacceptedParticipation() {
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
        for ( UserParticipation userParticipation
                : participationManager.getParticipationsAsAgent( agent, communityService )) {
            if ( !userParticipation.isAccepted() ) {
                String participantFullName = userParticipation.getParticipantFullName( communityService );
                Issue issue = makeIssue( communityService,Issue.COMPLETENESS, agent );
                communityService.getUserRecordService().getUserWithIdentity( userParticipation.getUsername() );
                issue.setDescription(
                        participantFullName
                                + " has not yet accepted to participate as \""
                                + agent.getName()
                                + "\" as requested by "
                                + userParticipation.getUserFullName( communityService )
                                + " on "
                                + new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( userParticipation.getCreated() ) );
                issue.setSeverity( Level.High ); // todo - use max failure impact of assigned tasks
                issue.setRemediation(
                        "Remind "
                        + participantFullName
                        + " of the participation request\n"
                        + "or withdraw the participation request."
                );
                issues.add( issue );
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
        return "User participation request not accepted";
    }
}
