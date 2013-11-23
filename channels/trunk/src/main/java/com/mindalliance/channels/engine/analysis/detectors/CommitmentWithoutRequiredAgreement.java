/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Agreement;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;

import java.util.ArrayList;
import java.util.List;

/**
 * Communication commitment without a required sharing agreement.
 */
public class CommitmentWithoutRequiredAgreement extends AbstractIssueDetector {

    public CommitmentWithoutRequiredAgreement() {
    }

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = (Organization) modelObject;
        if ( org.isActual() && org.isEffectiveAgreementsRequired() ) {
            List<Agreement> confirmed = org.getAgreements();
            for ( Agreement agreement : queryService.findAllImpliedAgreementsOf( org,
                                                                                 queryService.getAssignments( false ),
                                                                                 queryService.findAllFlows() ) )
            {
                if ( !confirmed.contains( agreement ) ) {
                    DetectedIssue issue = makeIssue( communityService, Issue.COMPLETENESS, org );
                    issue.setDescription( "Not confirmed: " + agreement.getSummary( org ) + '.' );
                    issue.setRemediation( "Confirm the agreement,\n" + "or remove the requirement for agreements from "
                                          + agreementRequiringOrganization( org ).getName() );
                    issue.setSeverity( Level.Low );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    private static Organization agreementRequiringOrganization( Organization org ) {
        return org.isAgreementsRequired() ? org : org.agreementRequiringParent();
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Organization;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Communication commitment without required agreement";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
