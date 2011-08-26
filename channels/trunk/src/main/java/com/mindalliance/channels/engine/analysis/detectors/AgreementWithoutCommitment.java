package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Agreement;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.engine.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Agreement without commitment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 23, 2009
 * Time: 11:02:12 AM
 */
public class AgreementWithoutCommitment extends AbstractIssueDetector {

    public AgreementWithoutCommitment() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Organization organization = (Organization) modelObject;
        final QueryService queryService = getQueryService();
        for ( final Agreement agreement : organization.getAgreements() ) {
            boolean exists = CollectionUtils.exists(
                    queryService.findAllCommitmentsOf(
                            organization,
                            queryService.getAssignments( false ),
                            queryService.findAllFlows() ),
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return queryService.covers( agreement,
                                    ( (Commitment) object ) );
                        }
                    }
            );
            if ( !exists ) {
                Issue issue = makeIssue( Issue.COMPLETENESS, organization );
                issue.setDescription( " No commitment is covered by "
                        + "\"" + agreement.getSummary( organization ) + "\"" );
                issue.setRemediation( "Unconfirm the agreement"
                        + "\nor add flows that imply a commitment covered by the agreement" );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return Organization.class.isAssignableFrom( modelObject.getClass() );
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
        return "Sharing agreement without commitment";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
