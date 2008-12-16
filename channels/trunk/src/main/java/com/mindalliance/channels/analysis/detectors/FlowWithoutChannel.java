package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Flow;

import java.util.List;
import java.util.ArrayList;

/**
 * Detects issue where a flow has no defined channel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 2:09:12 PM
 */
public class FlowWithoutChannel extends AbstractIssueDetector {

    public FlowWithoutChannel() {
    }

    /** {@inheritDoc} */
    public List<Issue> doDetectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        String channel = flow.getChannel();
        if ( channel == null || channel.trim().isEmpty() ) {
            Issue issue = new Issue( Issue.DEFINITION, modelObject, "channel" );
            issue.setDescription( "The channel is missing." );
            issue.setRemediation( "Provide a channel." );
            issues.add( issue );
        }
        return issues;
    }

    /** {@inheritDoc} */
     public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow;
    }

    /** {@inheritDoc} */
    public String getTestedProperty() {
        return "channel";
    }
}
