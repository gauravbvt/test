package com.mindalliance.channels.core.community.participation.issues.detectors;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.issues.ParticipationIssue;
import com.mindalliance.channels.core.community.participation.issues.ParticipationIssueDetector;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Organization;

import java.util.ArrayList;
import java.util.List;

/**
 * No organization participating as placeholder.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/8/13
 * Time: 1:51 PM
 */
public class MissingOrganizationParticipation implements ParticipationIssueDetector {

    public MissingOrganizationParticipation() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Organization && ((Organization)identifiable).isPlaceHolder();
    }

    @Override
    public String getKind() {
        return "No organization participates as placeholder";
    }

    @Override
    public List<ParticipationIssue> detectIssues( Identifiable identifiable, PlanCommunity planCommunity ) {
        List<ParticipationIssue> issues = new ArrayList<ParticipationIssue>(  );
        Organization placeholder = (Organization)identifiable;
        int count = planCommunity.getParticipationManager()
                .findAgenciesParticipatingAs( placeholder, planCommunity ).size();
        if ( count == 0 ) {
            ParticipationIssue issue = new ParticipationIssue( placeholder, this );
            issue.setDescription( "No organization participates as placeholder " + placeholder.getName() );
            issue.addRemediationOption( "Register an organization as " + placeholder.getName() );
            issues.add(  issue );
        }
        return issues;
    }
}
