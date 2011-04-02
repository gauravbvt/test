package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Channelable;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Incorrect channel";
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Channelable channelable = (Channelable) modelObject;
        List<Channel> channels = channelable.getEffectiveChannels();
        for ( Channel channel : channels ) {
            // Check for valid medium and if valid for valid address.
            String problem;
            String remediation = "";
            if ( channel.getMedium() == null || channel.getMedium().isUnknown() ) {
                problem = "Channel is missing a transmission medium.";
                remediation = "Provide a valid medium for the channel.";
            } else if ( channel.getMedium().hasInvalidAddressPattern() ) {
                problem = " Medium " + channel.getMedium().getName() + " has an invalid address pattern";
                remediation = "Fix the address pattern"
                        + "\nor remove the address pattern from the definition of "
                        + channel.getMedium().getName();
            } else {
                problem = channelable.validate( channel );
                if ( problem != null ) {
                    remediation = "Provide a correct address for " + channel.getMedium() + ".";
                } else {
                    if ( channel.getAddress().isEmpty()
                            && modelObject.isEntity()
                            && ( (ModelEntity) modelObject ).isActual() ) {
                        problem = "The " + channel.getMedium().getName() + " channel's address is empty.";
                        remediation = "Enter a valid address.";
                    }
                }
            }
            if ( problem != null ) {
                Issue issue = makeIssue( Issue.VALIDITY, modelObject );
                issue.setDescription( channel.toString() + ": " + problem );
                issue.setRemediation( remediation );
                issue.setSeverity( getSeverity( channelable ) );
                issues.add( issue );
            }
            // Check for duplicate channels.
            if ( CollectionUtils.cardinality( channel, channels ) > 1 ) {
                Issue issue = makeIssue( Issue.VALIDITY, modelObject );
                issue.setDescription( channel.toString() + " is repeated." );
                issue.setRemediation( "Remove this channel." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;

    }

    private Level getSeverity( Channelable channelable ) {
        if ( channelable instanceof Flow ) {
            Flow flow = (Flow) channelable;
            if ( flow.getTarget().isPart() ) {
                return getQueryService().computePartPriority( (Part) flow.getTarget() );
            } else {
                return Level.Low;
            }
        } else {
            return Level.Low;
        }
    }

}
