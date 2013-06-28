package com.mindalliance.channels.core.community.participation.issues.detectors;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.participation.issues.ParticipationIssue;
import com.mindalliance.channels.core.community.participation.issues.ParticipationIssueDetector;
import com.mindalliance.channels.core.model.Identifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * Agency has an invalid parent.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/8/13
 * Time: 10:28 AM
 */
public class InvalidAgencyParent implements ParticipationIssueDetector {

    public InvalidAgencyParent() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Agency;
    }

    @Override
    public String getKind() {
        return "Agency has invalid parent";
    }

    @Override
    public List<ParticipationIssue> detectIssues( Identifiable identifiable, CommunityService communityService ) {
        List<ParticipationIssue> issues = new ArrayList<ParticipationIssue>();
        Agency agency = (Agency) identifiable;
        Agency parent = agency.getParent( communityService );
        if ( parent != null && !communityService.canHaveParentAgency( agency.getName(), parent.getName() ) ) {
            ParticipationIssue issue = new ParticipationIssue( identifiable, this );
            issue.setDescription( parent.getName() + " is not a valid parent organization for " + agency.getName() );
            issue.addRemediationOption( "Remove " + parent.getName() + " as parent organization" );
            issue.addRemediationOption( "Give " + agency.getName() + " a different and valid parent" );
            issues.add( issue );
        }
        return issues;
    }
}
