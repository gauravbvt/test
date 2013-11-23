/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Agreement;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * An agreement by an organization is encompassed by another from the same or a parent organization.
 */
public class AgreementEncompassedByOther extends AbstractIssueDetector {

    public AgreementEncompassedByOther() {
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Organization organization = (Organization) modelObject;
        for ( Agreement agreement : organization.getAgreements() ) {
            applyTest( agreement, organization, organization, issues, communityService );
            for ( Organization ancestor : organization.ancestors() ) {
                applyTest( agreement, organization, ancestor, issues, communityService );
            }
        }
        return issues;
    }

    private void applyTest( Agreement agreement, Organization organization, Organization otherOrg, List<Issue> issues,
                            CommunityService communityService ) {
        for ( Agreement otherAgreement : otherOrg.getAgreements() ) {
            if ( !organization.equals( otherOrg ) || !agreement.equals( otherAgreement ) ) {
                if ( communityService.getPlanService().encompasses( otherAgreement, agreement ) ) {
                    Issue issue = makeIssue( communityService, Issue.COMPLETENESS, organization );
                    issue.setDescription( "\"" + otherAgreement.getSummary( otherOrg )
                            + "\" encompasses \"" + agreement.getSummary( organization ) + "\"" );
                    issue.setRemediation( "Unconfirm \"" + agreement.getSummary( organization ) + "\""
                            + "\nor unconfirm \"" + otherAgreement.getSummary( otherOrg ) + "\""
                    );
                    issue.setSeverity( Level.Low );
                    issues.add( issue );
                }
            }
        }
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return Organization.class.isAssignableFrom( modelObject.getClass() );
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Redundant sharing agreement ";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
