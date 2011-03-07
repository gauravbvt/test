package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Channelable;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.query.Assignments;
import com.mindalliance.channels.query.DefaultQueryService;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Commitment implied in a flow is missing a valid channel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 9, 2009
 * Time: 10:37:02 AM
 */
public class CommittmentWithoutRequiredUnicastChannel extends AbstractIssueDetector {

    @Override
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        if ( flow.isSharing() ) {
            QueryService queryService = getQueryService();
            Set<Channelable> contactedEntities = new HashSet<Channelable>();
            Assignments assignments = queryService.getAssignments( false );
            for ( Commitment commitment : queryService.findAllCommitments( flow,
                                                                           false, assignments ) )
                contactedEntities.add( contactedEntity( commitment ) );

            final Place locale = getPlan().getLocale();
            for ( Channelable contacted : contactedEntities ) {
                for ( final Channel flowChannel : flow.getEffectiveChannels() ) {
                    if ( flowChannel.isUnicast() && !flowChannel.isDirect() ) {
                        boolean hasValidChannel = CollectionUtils.exists(
                            contacted.getEffectiveChannels(),
                            new Predicate() {
                                @Override
                                public boolean evaluate( Object object ) {
                                  Channel channel = (Channel) object;
                                  return channel.isValid()
                                         && channel.getMedium()
                                            .narrowsOrEquals( flowChannel.getMedium(), locale );
                              }
                          } );
                        if ( !hasValidChannel ) {
                            Issue issue = makeIssue( Issue.COMPLETENESS, flow );
                            issue.setDescription(
                                "There is no valid channel for contacting " + contacted.getName()
                                + " via " + flowChannel.getMedium() );
                            issue.setRemediation(
                                "Make sure that " + contacted.getName() + " can be contacted via "
                                + flowChannel.getMedium()
                                + " with a correct address if one is required" + "\nor remove "
                                + flowChannel.getMedium() + " from the flow" );
                            issue.setSeverity( getTaskFailureSeverity( (Part) flow.getTarget() ) );
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
        return "Sharing commitment without contact info";
    }
}
