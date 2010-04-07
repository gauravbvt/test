package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Agreement;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

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
        for ( final Commitment commitment : queryService.findAllCommitmentsOf( org ) ) {
            if ( org.isAgreementsRequired()
                    && commitment.isBetweenOrganizations() ) {
                if ( !CollectionUtils.exists(
                        org.getAgreements(),
                        new Predicate() {
                            public boolean evaluate( Object object ) {
                                return ( (Agreement) object ).covers( commitment, queryService );
                            }
                        }
                ) ) {
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
    protected String getLabel() {
        return "Sharing commitment without required agreement";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
