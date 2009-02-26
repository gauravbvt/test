package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.Channel;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 25, 2009
 * Time: 9:07:21 PM
 */
public class InvalidChannel extends AbstractIssueDetector {
    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Channelable;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }

    /**
     * Do the work of detecting issues about the model object.
     *
     * @param modelObject -- the model object being analyzed
     * @return -- a list of issues
     */
    protected List<Issue> doDetectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Channelable channelable = (Channelable) modelObject;
        List<Channel> channels = channelable.getEffectiveChannels();
        for ( Channel channel : channels ) {
            if ( !channel.isValid() ) {
                Issue issue = new DetectedIssue( DetectedIssue.DEFINITION, modelObject );
                issue.setDescription( channel.toString() + " is not a valid channel." );
                String remediation;
                if ( channel.getMedium() == null ) {
                    remediation = "Provide a valid medium for the channel.";
                } else {
                    remediation = "Provide a correct address, handle, location etc. for " + channel.getMedium() + ".";
                }
                issue.setRemediation( remediation );
                issue.setSeverity( Issue.Level.Major );
                issues.add( issue );
            }
        }
        return issues;

    }
}
