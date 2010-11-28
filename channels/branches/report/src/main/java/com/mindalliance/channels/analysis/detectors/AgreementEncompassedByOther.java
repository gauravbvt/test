package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Agreement;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;

import java.util.ArrayList;
import java.util.List;

/**
 * An agreement by an organization is encompassed by another from the same or a parent organization.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 23, 2009
 * Time: 9:07:47 AM
 */
public class AgreementEncompassedByOther extends AbstractIssueDetector {

    public AgreementEncompassedByOther() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Organization organization = (Organization) modelObject;
        for ( Agreement agreement : organization.getAgreements() ) {
            applyTest( agreement, organization, organization, issues );
            for ( Organization ancestor : organization.ancestors() ) {
                applyTest( agreement, organization, ancestor, issues );
            }
        }
        return issues;
    }

    private void applyTest(
            Agreement agreement,
            Organization organization,
            Organization otherOrg,
            List<Issue> issues ) {
        for ( Agreement otherAgreement : otherOrg.getAgreements() ) {
            if ( !organization.equals( otherOrg ) || !agreement.equals( otherAgreement ) ) {
                if ( getQueryService().encompasses( otherAgreement, agreement ) ) {
                    Issue issue = makeIssue( Issue.COMPLETENESS, organization );
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
        return "Redundant sharing agreement ";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
