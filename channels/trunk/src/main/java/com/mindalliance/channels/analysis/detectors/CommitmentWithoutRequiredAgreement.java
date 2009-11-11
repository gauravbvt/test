package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;

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
        List<Issue> issues = new ArrayList<Issue>();
        // TODO redefine

/*        Flow flow = (Flow) modelObject;
        Part source = (Part) flow.getSource();
        Organization org = source.getOrganization();
        if ( org != null && org.isAgreementsRequired() ) {
            Organization otherOrg = ( (Part) flow.getTarget() ).getOrganization();
            if ( otherOrg != null && !org.equals( otherOrg ) ) {
                boolean hasMOU = CollectionUtils.exists(
                        flow.getAttachments(),
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                return ( (Attachment) obj ).getType() == Attachment.Type.MOU;
                            }
                        }
                );
                if ( !hasMOU ) {
                    DetectedIssue issue = makeIssue( Issue.COMPLETENESS, flow );
                    issue.setDescription( "Sharing commitments from "
                            + org.getName()
                            + " to another organization"
                            + " must be backed by a sharing agreement." );
                    issue.setRemediation( "Attach an agreement to this sharing commitment,\n"
                            + "or remove the requirement for agreements for "
                            + org.getName() );
                    issue.setSeverity( Issue.Level.Major );
                    issues.add( issue );
                }
            }
        }*/

        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow
                && ( (Flow) modelObject ).isSharing();
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
