/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Commitment implied in a flow is missing a valid channel.
 */
public class CommittmentWithoutRequiredUnicastChannel extends AbstractIssueDetector {

    @Override
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        if ( flow.isSharing() ) {
            Set<Channelable> contactedEntities = new HashSet<Channelable>();
            Assignments assignments = queryService.getAssignments( false );
            for ( Commitment commitment : queryService.findAllCommitments( flow, false, assignments ) )
                contactedEntities.add( commitment.getContactedEntity() );

            final Place locale = queryService.getPlan().getLocale();
            for ( Channelable contacted : contactedEntities ) {
                for ( final Channel flowChannel : flow.getEffectiveChannels() ) {
                    if ( flowChannel.isUnicast() && !flowChannel.isDirect() ) {
                        boolean hasValidChannel =
                                CollectionUtils.exists( contacted.getEffectiveChannels(), new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        Channel channel = (Channel) object;
                                        return channel.isValid()
                                               && channel.getMedium().narrowsOrEquals( flowChannel.getMedium(),
                                                                                       locale );
                                    }
                                } );
                        if ( !hasValidChannel ) {
                            Issue issue = makeIssue( queryService, Issue.COMPLETENESS, flow );
                            issue.setDescription(
                                    "There is no valid channel for contacting " + contacted.getName() + " via "
                                    + flowChannel.getMedium() );
                            issue.setRemediation( "Make sure that " + contacted.getName() + " can be contacted via "
                                                  + flowChannel.getMedium()
                                                  + " with a correct address if one is required" + "\nor remove "
                                                  + flowChannel.getMedium() + " from the flow" );
                            issue.setSeverity( computeTaskFailureSeverity( queryService, (Part) flow.getTarget() ) );
                            issues.add( issue );
                        }
                    }
                }
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Flow;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Sharing commitment without contact info";
    }
}
