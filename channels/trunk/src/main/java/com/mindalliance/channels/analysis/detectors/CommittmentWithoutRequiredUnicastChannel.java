package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Channelable;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Commitment implied in a flow is missing a valid channel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 9, 2009
 * Time: 10:37:02 AM
 */
public class CommittmentWithoutRequiredUnicastChannel extends AbstractIssueDetector {

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        if ( flow.isSharing() ) {
            final QueryService queryService = getQueryService();
            for ( final Commitment commitment : queryService.findAllCommitments( flow ) ) {
                Channelable contacted = contactedEntity( commitment );
                for ( final Channel flowChannel : flow.getEffectiveChannels() ) {
                    if ( flowChannel.isUnicast() && !flowChannel.isDirect() ) {
                        boolean hasValidChannel = CollectionUtils.exists(
                                contacted.getEffectiveChannels(),
                                new Predicate() {
                                    public boolean evaluate( Object object ) {
                                        Channel channel = (Channel) object;
                                        return channel.isValid()
                                                && channel.getMedium().narrowsOrEquals( flowChannel.getMedium() );
                                    }
                                } );
                        if ( !hasValidChannel ) {
                            Issue issue = makeIssue( Issue.VALIDITY, flow );
                            issue.setDescription( "There is no valid channel for contacting "
                                    + contacted.getName()
                                    + " via "
                                    + flowChannel.getMedium()
                            );
                            issue.setRemediation( "Make sure that "
                                    + contacted.getName()
                                    + " can be contacted via "
                                    + flowChannel.getMedium()
                                    + " with a correct address if one is required"
                                    + "\nor remove "
                                    + flowChannel.getMedium()
                                    + " from the flow"
                            );
                            issue.setSeverity( getQueryService().getPartPriority( (Part) flow.getTarget() ) );
                            issues.add( issue );
                        }
                    }
                }
            }
        }
        return issues;
    }

    private Channelable contactedEntity( Commitment commitment ) {
        if ( commitment.getSharing().isAskedFor() ) {
            return commitment.getCommitter().getChannelable();
        } else {
            return commitment.getBeneficiary().getChannelable();
        }
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
        return "Commitment without needed contact info";
    }
}
