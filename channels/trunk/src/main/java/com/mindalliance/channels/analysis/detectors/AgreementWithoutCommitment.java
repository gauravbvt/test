package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Agreement;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
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
                    queryService.findAllCommitmentsOf( organization ),
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return agreement.covers( ( (Commitment) object ), queryService );
                        }
                    }
            );
            if ( !exists ) {
                Issue issue = makeIssue( Issue.COMPLETENESS, organization );
                issue.setDescription( " No commitment is covered by "
                        + "\"" + agreement.getSummary( organization ) + "\"" );
                issue.setRemediation( "Unconfirm the agreement"
                        + "\nor add flows that imply a commitment covered by the agreement" );
                issue.setSeverity( Issue.Level.Minor );
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
    protected String getLabel() {
        return "Sharing agreement without commitment";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
