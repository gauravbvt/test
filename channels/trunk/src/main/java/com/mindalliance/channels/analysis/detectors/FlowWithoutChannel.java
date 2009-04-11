package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.Medium;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

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
        if ( needsAtLeastOneChannel( flow ) ) {
            // There is no channel in a flow that requires one
            List<Channel> flowChannels = flow.getEffectiveChannels();
            if ( flowChannels.isEmpty() ) {
                issues.add( createIssue( modelObject,
                        Issue.Level.Severe,
                        "Flow requires a channel.",
                        "Provide at least one channel." ) );
            } else if ( !flow.canBeUnicast() ) {
                // Communicating with a non-unicastable using a unicast channel for which
                // a matching actor doesn't have a channel defined with same medium.
                Set<Medium> media = getUnicastMedia( flow );
                ResourceSpec partSpec = flow.getContactedPart().resourceSpec();
                for ( Actor actor : getDqo().findAllActors( partSpec ) ) {
                    ResourceSpec actorSpec = new ResourceSpec( partSpec );
                    actorSpec.setActor( actor );

                    for ( Channel channel : getDqo().findAllChannelsFor( actorSpec ) ) {
                        Medium channelMedium = channel.getMedium();
                        if ( media.contains( channelMedium ) && !channel.isValid() )
                            issues.add( createIssue(
                                    modelObject,
                                    Issue.Level.Major,
                                    MessageFormat.format(
                                        "{0} may be involved and has no valid {1} contact info.",
                                         actor.getName(),
                                         channel.getMedium() ),
                                    MessageFormat.format(
                                         "Add a {0} contact info to {1}",
                                         channel.getMedium(),
                                         actor.getName() ) ) );
                    }
                }
            }
        }
        return issues;
    }

    private static Set<Medium> getUnicastMedia( Flow flow ) {
        Set<Medium> media = EnumSet.noneOf( Medium.class );
        for ( Channel channel : flow.getEffectiveChannels() ) {
            Medium medium = channel.getMedium();
            if ( medium.isUnicast() )
                media.add( medium );
        }
        return media;
    }

    private static DetectedIssue createIssue(
            ModelObject modelObject, Issue.Level severity, String description,
            String remediation ) {
        DetectedIssue issue = new DetectedIssue( Issue.DEFINITION, modelObject );
        issue.setDescription( description );
        issue.setRemediation( remediation );
        issue.setSeverity( severity );
        return issue;
    }

    private static boolean needsAtLeastOneChannel( Flow flow ) {
        return !flow.getTarget().isConnector() && flow.isNotification()
            || !flow.getSource().isConnector() && flow.isAskedFor();
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
}
