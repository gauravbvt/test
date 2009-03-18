package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.AbstractUnicastChannelable;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;

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
    public List<Issue> doDetectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        if ( needsAtLeastOneChannel( flow ) ) {
            // There is no channel in a flow that requires one
            if ( flow.getEffectiveChannels().isEmpty() ) {
                DetectedIssue issue = new DetectedIssue( Issue.DEFINITION, modelObject );
                issue.setDescription( "Flow requires a channel." );
                issue.setRemediation( "Provide at least one channel." );
                issue.setSeverity( Issue.Level.Severe );
                issues.add( issue );
            } else {
                // Communicating with a non-unicastable using a unicast channel for which
                // a matching actor doesn't have a channel defined with same medium.
                if ( !flow.canBeUnicast() ) {
                    final ResourceSpec partResourceSpec = partNeedingChannel( flow ).resourceSpec();
                    List<Actor> actors = getService().findAllActors( partResourceSpec );
                    for ( Actor actor : actors ) {
                        for ( Channel flowChannel : flow.getEffectiveChannels() ) {
                            boolean channelUndefined = true;
                            for ( Channel actorChannel : actor.getEffectiveChannels() ) {
                                if ( actorChannel.getMedium() == flowChannel.getMedium() && actorChannel.isValid() ) {
                                    channelUndefined = false;
                                }
                            }
                            if ( channelUndefined ) {
                                DetectedIssue issue = new DetectedIssue( Issue.DEFINITION, modelObject );
                                issue.setDescription(
                                        actor.getName()
                                                + " may be involved and has no valid "
                                                + flowChannel.getMedium()
                                                + " contact info." );
                                issue.setRemediation(
                                        "Add a "
                                                + flowChannel.getMedium()
                                                + " contact info to "
                                                + actor.getName() );
                                issue.setSeverity( Issue.Level.Major );
                                issues.add( issue );
                            }
                        }
                    }
                }
            }
        }
        return issues;
    }

    private boolean needsAtLeastOneChannel( Flow flow ) {
        return ( !flow.getTarget().isConnector() && flow.isNotification() )
                || ( !flow.getSource().isConnector() && flow.isAskedFor() );
    }

    private Part partNeedingChannel( Flow flow ) {
        return flow.isNotification() ? (Part) flow.getTarget() : (Part) flow.getSource();
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
