package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Sharing flow without commitment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 10, 2009
 * Time: 1:15:20 PM
 */
public class SharingWithoutCommitments extends AbstractIssueDetector {

    public SharingWithoutCommitments() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        List<Commitment> commitments = getQueryService().findAllCommitments( flow );
        if ( commitments.isEmpty() ) {
            Issue issue = makeIssue( Issue.COMPLETENESS, flow );
            issue.setDescription( "No commitment is implied by this sharing flow" );
            StringBuilder sb = new StringBuilder();
            sb.append( "Modify the specifications for source and target tasks so that they have assignments" );
            sb.append( "\nor profile individuals to match source and target task specifications" );
            if ( flow.isClassificationsLinked() ) {
                sb.append( "\nor declassify elements of information" );
                sb.append( "\nor make sure otherwise commitable individuals have the required clearances" );
            }
            issue.setRemediation( sb.toString() );
            issue.setSeverity( Level.Low );
            issues.add( issue );
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow && ( (Flow) modelObject ).isSharing();
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
        return "Sharing flow with no commitments";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }

}
