package com.mindalliance.channels.core.community.participation.issues.detectors;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.participation.issues.ParticipationIssue;
import com.mindalliance.channels.core.community.participation.issues.ParticipationIssueDetector;
import com.mindalliance.channels.core.model.Identifiable;

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
public class TooManyUsersParticipatingAsAgent implements ParticipationIssueDetector {

    public TooManyUsersParticipatingAsAgent() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Agent;
    }

    @Override
    public String getKind() {
        return "Too many users participate as agent";
    }

    @Override
    public List<ParticipationIssue> detectIssues( Identifiable identifiable, CommunityService communityService ) {
        List<ParticipationIssue> issues = new ArrayList<ParticipationIssue>();
        Agent agent = (Agent) identifiable;
        int maxParticipation = agent.getMaxParticipation();
        if ( maxParticipation != -1 ) {
            int count = communityService.getUserParticipationService().findUsersActivelyParticipatingAs( agent, communityService ).size();
            if ( count > maxParticipation ) {
                ParticipationIssue issue = new ParticipationIssue( agent, this );
                issue.setDescription( count + " users participate as " + agent.getName()
                        + "; maximum is " + maxParticipation );
                issue.addRemediationOption( "Remove a user participation as " + agent.getName() );
                issue.addRemediationOption( "Allow more users to participate as " + agent.getName() );
                issues.add( issue );
            }
        }
        return issues;
    }
}
