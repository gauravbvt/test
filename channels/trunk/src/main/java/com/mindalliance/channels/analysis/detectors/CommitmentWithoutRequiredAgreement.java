package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.attachments.Attachment;

import java.util.List;
import java.util.ArrayList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * Sharing commitment without a required MOU.
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
        Flow flow = (Flow) modelObject;
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
                            + " must be subject to an MOU." );
                    issue.setRemediation( "Attach an MOU to this sharing commitment, "
                            + "or remove the requirement for MOUs for "
                            + org.getName() );
                    issue.setSeverity( Issue.Level.Major );
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
        return modelObject instanceof Flow
                && ( (Flow) modelObject ).isSharingCommitment();
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
        return "Sharing commitment without required MOU";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
