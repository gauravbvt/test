package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Agreement;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;
import com.mindalliance.channels.engine.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Sharing commitment without a required sharing agreement.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 17, 2009
 * Time: 4:12:26 PM
 */
public class CommitmentWithoutRequiredAgreement extends AbstractIssueDetector {

    public CommitmentWithoutRequiredAgreement() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        final QueryService queryService = getQueryService();
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = (Organization) modelObject;
        if ( org.isActual() && org.isEffectiveAgreementsRequired() ) {
            List<Agreement> confirmed = org.getAgreements();
            for ( Agreement agreement : getQueryService().findAllImpliedAgreementsOf(
                    org,
                    queryService.getAssignments( false ),
                    queryService.findAllFlows() ) ) {
                if ( !confirmed.contains( agreement ) ) {
                    DetectedIssue issue = makeIssue( Issue.COMPLETENESS, org );
                    issue.setDescription( "Not confirmed: "
                            + agreement.getSummary( org )
                            + "." );
                    issue.setRemediation( "Confirm the agreement,\n"
                            + "or remove the requirement for agreements from "
                            + agreementRequiringOrganization( org ).getName() );
                    issue.setSeverity( Level.Low );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    private Organization agreementRequiringOrganization( Organization org ) {
        if ( org.isAgreementsRequired() )
            return org;
        else
            return org.agreementRequiringParent();
    }


    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Organization;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected String getKindLabel() {
        return "Sharing commitment without required agreement";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
