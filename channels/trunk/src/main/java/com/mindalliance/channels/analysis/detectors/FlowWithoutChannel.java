package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Medium;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;

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
                        getSeverity( flow ),
                        "Flow requires a channel.",
                        "Provide at least one channel." ) );

            } else if ( !flow.canBeUnicast() ) {
                // Communicating with a non-unicastable using a unicast channel for which
                // a matching actor doesn't have a channel defined with same medium.
                Set<Medium> media = getUnicastMedia( flow );
                ResourceSpec partSpec = flow.getContactedPart().resourceSpec();
                // If both any actor and any organization, don't bother with missing addresses
                if ( !( partSpec.getActor() == null && partSpec.getOrganization() == null ) ) {
                    List<Actor> actors = getQueryService().findAllActualActors( partSpec );
                    if ( actors.isEmpty() ) {
                        issues.addAll( findIssues( modelObject, partSpec, media ) );
                    } else
                        for ( Actor actor : actors ) {
                            ResourceSpec actorSpec = new ResourceSpec( partSpec );
                            actorSpec.setActor( actor );
                            issues.addAll( findIssues( modelObject, actorSpec, media ) );
                        }
                }
            }
        }
        return issues;
    }

    private Issue.Level getSeverity( Flow flow ) {
        if ( flow.getTarget().isPart() ) {
            return getQueryService().getPartPriority( (Part) flow.getTarget() );
        } else {
            return Issue.Level.Minor;
        }
    }

    private List<Issue> findIssues(
            ModelObject modelObject, ResourceSpec actorSpec, Set<Medium> media ) {

        List<Issue> result = new ArrayList<Issue>();
        for ( Channel channel : getQueryService().findAllChannelsFor( actorSpec ) ) {
            Medium channelMedium = channel.getMedium();
            if ( media.contains( channelMedium ) && !channel.isValid() ) {
                result.add( createIssue(
                        modelObject,
                        getSeverity( (Flow) modelObject ),
                        MessageFormat.format(
                                "{0} may be involved and has no valid {1} contact info.",
                                actorSpec.toString(),
                                channel.getMedium() ),
                        MessageFormat.format(
                                "Add a {0} contact info to {1}",
                                channel.getMedium(),
                                actorSpec.toString() ) ) );
            }
        }
        return result;
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

    private DetectedIssue createIssue(
            ModelObject modelObject, Issue.Level severity, String description,
            String remediation ) {
        DetectedIssue issue = makeIssue( Issue.COMPLETENESS, modelObject );
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

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Flow without channel";
    }
}
