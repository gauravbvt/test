package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 25, 2009
 * Time: 3:41:22 PM
 */
public class FlowViolatesPolicy extends AbstractIssueDetector {

    public FlowViolatesPolicy() {
    }

    /**
     * Find all flows with attachement(s) indicating policy violations
     *
     * @param modelObject -- the model object being analyzed
     * @return -- a list of issues
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        List<Attachment> attachments = flow.getAttachments();
        for ( Attachment attachment : attachments ) {
            if ( attachment.isPolicyViolation() ) {
                Issue issue = makeIssue( Issue.COMPLETENESS, flow );
                issue.setDescription( "Would violate policy per \"" + attachment.getUrl() + "\"." );
                issue.setRemediation( "Change the sharing flow\nor remove the sharing flow\nor change the policy." );
                if ( flow.getTarget().isPart() ) {
                    issue.setSeverity( getQueryService().computePartPriority( (Part) flow.getTarget() ) );
                } else {
                    issue.setSeverity( Level.Medium );
                }
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow;
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
        return "Policy prohibits sharing";
    }

    /**
      * {@inheritDoc}
      */
    public boolean canBeWaived() {
        return true;
    }
}
