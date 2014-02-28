/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Agreement;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Agreement without commitment.
 */
public class AgreementWithoutCommitment extends AbstractIssueDetector {

    public AgreementWithoutCommitment() {
    }

    @Override
    public List<Issue> detectIssues( final CommunityService communityService, Identifiable modelObject ) {
        final QueryService queryService = communityService.getModelService();
        List<Issue> issues = new ArrayList<Issue>();
        Organization organization = (Organization) modelObject;
        for ( final Agreement agreement : organization.getAgreements() ) {
            boolean exists = CollectionUtils.exists( queryService.findAllCommitmentsOf( organization,
                                                                                        queryService.getAssignments(
                                                                                                false ),
                                                                                        queryService.findAllFlows() ),
                                                     new Predicate() {
                                                         @Override
                                                         public boolean evaluate( Object object ) {
                                                             return queryService.covers( agreement,
                                                                                         ( (Commitment) object ) );
                                                         }
                                                     } );
            if ( !exists ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, organization );
                issue.setDescription(
                        " No commitment is covered by " + "\"" + agreement.getSummary( organization ) + "\"" );
                issue.setRemediation(
                        "Unconfirm the agreement" + "\nor add flows that imply a commitment covered by the agreement" );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
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
        return "Sharing agreement without commitment";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
