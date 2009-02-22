package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.pages.Project;

import java.util.List;
import java.util.ArrayList;

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
    // An issue when no channel in flow if contacting a role
    // and there is actors matching resource
    // or not all such actors spec have contact info (resourceSpec-assigned channels
    public List<Issue> doDetectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        if ( needsAtLeastOneChannel( flow ) && flow.getEffectiveChannels().isEmpty() ) {
            Part part = partNeedingChannel( flow );
            if ( part.isOnlyRole() ) {
                ResourceSpec partSpec = part.resourceSpec();
                List<Actor> actorsInRole = Project.service().
                        findAllActors( partSpec  );
                if ( actorsInRole.isEmpty() ) {
                    DetectedIssue issue = new DetectedIssue( DetectedIssue.DEFINITION, modelObject );
                    issue.setDescription(
                            "Flow involves "
                                    + partSpec
                                    + " and there is no known actor in this role." );
                    issue.setRemediation( "Provide at least one (broadcast) channel, "
                            + "or define actors in this role with contact info." );
                    issue.setSeverity( Issue.Level.Severe );
                    issues.add( issue );
                } else {
                    for ( Actor actor : actorsInRole ) {
                        List<Channel> actorChannels = ResourceSpec.with( actor ).allChannels();
                        if ( actorChannels.isEmpty() ) {
                            DetectedIssue issue = new DetectedIssue( DetectedIssue.DEFINITION, modelObject );
                            issue.setDescription( "Flow involves "
                                    + partSpec
                                    + " and "
                                    + actor.getName()
                                    + " in this role has no contact information." );
                            issue.setRemediation( "Provide contact information for "
                                    + actor.getName()
                                    + ", or provide at least one (broadcast) channel for the flow." );
                            issue.setSeverity( Issue.Level.Major );
                            issues.add( issue );
                        }
                    }
                }
            } else {
                DetectedIssue issue = new DetectedIssue( DetectedIssue.DEFINITION, modelObject );
                issue.setDescription( "Flow requires a channel." );
                issue.setRemediation( "Provide at least one channel." );
                issue.setSeverity( Issue.Level.Severe );
                issues.add( issue );
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
