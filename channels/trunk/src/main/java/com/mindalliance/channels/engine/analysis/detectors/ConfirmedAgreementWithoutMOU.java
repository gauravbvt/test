package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Agreement;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Confirmed agreement without MOU.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 8, 2010
 * Time: 7:54:18 PM
 */
public class ConfirmedAgreementWithoutMOU extends AbstractIssueDetector {
    public ConfirmedAgreementWithoutMOU() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = (Organization) modelObject;
        for ( Agreement agreement : org.getAgreements() ) {
            if ( !agreement.hasMOU() ) {
                Issue issue = makeIssue( communityService, Issue.COMPLETENESS, org );
                issue.setDescription( agreement.getSummary( org ) + " is not backed by an MOU." );
                issue.setSeverity( Level.Medium );
                issue.setRemediation( "Attach an MOU to the sharing agreement\nor unconfirm the sharing agreement" );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( Identifiable modelObject ) {
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
        return "Confirmed sharing agreement without MOU";
    }
}
