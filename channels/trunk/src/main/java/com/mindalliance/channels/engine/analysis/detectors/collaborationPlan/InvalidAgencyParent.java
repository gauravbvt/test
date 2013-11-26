package com.mindalliance.channels.engine.analysis.detectors.collaborationPlan;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

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
public class InvalidAgencyParent extends AbstractIssueDetector {

    public InvalidAgencyParent() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Agency;
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        Agency agency = (Agency) identifiable;
        Agency parent = agency.getParent( communityService );
        if ( parent != null && !communityService.canHaveParentAgency( agency.getName(), parent.getName() ) ) {
            Issue issue = makeIssue( communityService, Issue.VALIDITY, identifiable );
            issue.setDescription( parent.getName() + " is not a valid parent organization for " + agency.getName() );
            issue.setRemediation( "Remove " + parent.getName() + " as parent organization"
                    + "\nor give " + agency.getName() + " a different and valid parent" );
            issue.setSeverity( Level.Medium );
            issues.add( issue );
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return "parent";
    }

    @Override
    protected String getKindLabel() {
        return "Organization has invalid parent";
    }
}
