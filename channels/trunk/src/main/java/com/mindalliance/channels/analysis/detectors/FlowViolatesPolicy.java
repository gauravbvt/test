package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;

import java.util.ArrayList;
import java.util.Iterator;
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
        Iterator<Attachment> attachments = getAttachmentManager().attachments( flow.getId() );
        while ( attachments.hasNext() ) {
            Attachment attachment = attachments.next();
            if ( attachment.isPolicyViolation() ) {
                Issue issue = makeIssue( Issue.FLOW, flow );
                issue.setDescription( "Violates policy per \"" + attachment.getLabel() + "\"." );
                issue.setRemediation( "Change or remove flow, or change the policy." );
                issue.setSeverity( Issue.Level.Severe );
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

}
