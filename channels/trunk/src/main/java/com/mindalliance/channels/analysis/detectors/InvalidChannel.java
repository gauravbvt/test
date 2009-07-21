package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Channelable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Flow;
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
        return "Invalid channel";
    }

    /**
     * Do the work of detecting issues about the model object.
     *
     * @param modelObject -- the model object being analyzed
     * @return -- a list of issues
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Channelable channelable = (Channelable) modelObject;
        List<Channel> channels = channelable.getEffectiveChannels();
        for ( Channel channel : channels ) {
            String problem = channelable.validate( channel );
            if ( problem != null ) {
                Issue issue = makeIssue( Issue.VALIDITY, modelObject );
                issue.setDescription( channel.toString() + ": " + problem );
                String remediation;
                if ( channel.getMedium() == null ) {
                    remediation = "Provide a valid medium for the channel.";
                } else {
                    remediation = "Provide a correct address, handle, location etc. for " + channel.getMedium() + ".";
                }
                issue.setRemediation( remediation );
                issue.setSeverity( getSeverity( channelable ) );
                issues.add( issue );
            }
            if ( CollectionUtils.cardinality( channel, channels ) > 1 ) {
                Issue issue = makeIssue( Issue.VALIDITY, modelObject );
                issue.setDescription( channel.toString() + " is repeated." );
                issue.setRemediation( "Remove this channel." );
                issue.setSeverity( Issue.Level.Minor );
                issues.add( issue );
            }
        }
        return issues;

    }

    private Issue.Level getSeverity( Channelable channelable ) {
        if ( channelable instanceof Flow ) {
            Flow flow = (Flow) channelable;
            if ( flow.getTarget().isPart() ) {
                return getQueryService().getPartPriority( (Part) flow.getTarget() );
            } else {
                return Issue.Level.Minor;
            }
        } else {
            return Issue.Level.Minor;
        }
    }

}
