package com.mindalliance.channels.core.community.participation.issues.detectors;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.participation.issues.ParticipationIssue;
import com.mindalliance.channels.core.community.participation.issues.ParticipationIssueDetector;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

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
public class MissingUserParticipation implements ParticipationIssueDetector {

    public MissingUserParticipation() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Agent;
    }

    @Override
    public String getKind() {
        return "No user participates as agent";
    }

    @Override
    public List<ParticipationIssue> detectIssues( Identifiable identifiable, CommunityService communityService ) {
        List<ParticipationIssue> issues = new ArrayList<ParticipationIssue>();
        final Agent agent = (Agent) identifiable;
        int count = communityService.getParticipationManager().findAllUsersParticipatingAs( agent, communityService ).size();

        if ( count == 0 ) {
            ParticipationIssue issue = new ParticipationIssue( agent, this );
            issue.setDescription( "No user participates as " + agent.getName() );
            issue.addRemediationOption( "Request that a user participate as " + agent.getName() );
            issues.add( issue );
        }
        return issues;
    }

}
