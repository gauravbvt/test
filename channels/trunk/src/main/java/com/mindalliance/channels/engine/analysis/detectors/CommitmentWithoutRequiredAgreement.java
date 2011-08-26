package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
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
        if ( org.isActual() && org.isAgreementsRequired() ) {
            // todo - optimize this: only find commitments that cross org lines etc.
            List<Commitment> commitments = queryService.findAllCommitmentsOf(
                    org,
                    queryService.getAssignments( false ),
                    queryService.findAllFlows() );
            for ( final Commitment commitment : commitments ) {
                if ( queryService.isAgreementRequired( commitment )
                        && !queryService.isCoveredByAgreement( commitment ) ) {
                    DetectedIssue issue = makeIssue( Issue.COMPLETENESS, org );
                    issue.setDescription( commitment.toString()
                            + ", but this is not backed by a sharing agreement." );
                    issue.setRemediation( "Confirm an agreement covering this sharing commitment,\n"
                            + "or remove the requirement for agreements for "
                            + org.getName() );
                    issue.setSeverity( Level.Low );
                    issues.add( issue );
                }
            }
        }
        return issues;
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
