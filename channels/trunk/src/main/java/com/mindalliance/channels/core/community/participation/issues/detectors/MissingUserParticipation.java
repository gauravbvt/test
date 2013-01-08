package com.mindalliance.channels.core.community.participation.issues.detectors;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.issues.ParticipationIssue;
import com.mindalliance.channels.core.community.participation.issues.ParticipationIssueDetector;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Identifiable;
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
        return identifiable instanceof Actor;
    }

    @Override
    public String getKind() {
        return "No user participates as agent";
    }

    @Override
    public List<ParticipationIssue> detectIssues( Identifiable identifiable, PlanCommunity planCommunity ) {
        List<ParticipationIssue> issues = new ArrayList<ParticipationIssue>(  );
        Actor actor = (Actor)identifiable;
        if ( isEmployedByFixedOrganization( actor, planCommunity ) ) {
            int count = planCommunity.getUserParticipationService().findUsersParticipatingAs(
                    new Agent( actor ),
                    planCommunity
            ).size();
            if ( count == 0 ) {
                ParticipationIssue issue = new ParticipationIssue( actor, this );
                issue.setDescription( "No user participates as " + actor.getName() );
                issue.addRemediationOption( "Request that a user participate as " + actor.getName() );
                issues.add( issue );
            }
        }
        return issues;
    }

    private boolean isEmployedByFixedOrganization( Actor actor, PlanCommunity planCommunity ) {
        return CollectionUtils.exists(
                planCommunity.getPlanService().findAllEmploymentsForActor( actor ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (Employment)object ).getOrganization().isPlaceHolder();
                    }
                } );
    }
}
