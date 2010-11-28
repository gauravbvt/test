package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();

        Flow flow = (Flow) modelObject;
        // There is no channel in a flow that requires one
        List<Channel> flowChannels = flow.getEffectiveChannels();
        if ( flowChannels.isEmpty() ) {
            issues.add( createIssue( modelObject,
                    getSeverity( flow ),
                    "At least one channel is required.",
                    "Add a channel" ) );

        }
        return issues;
    }

    private Level getSeverity( Flow flow ) {
        if ( flow.isSharing() ) {
            return getSharingFailureSeverity( flow );
        } else {
            return Level.Low;
        }
    }


    private DetectedIssue createIssue(
            ModelObject modelObject, Level severity, String description,
            String remediation ) {
        DetectedIssue issue = makeIssue( Issue.COMPLETENESS, modelObject );
        issue.setDescription( description );
        issue.setRemediation( remediation );
        issue.setSeverity( severity );
        return issue;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTestedProperty() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Flow without channel";
    }
}
