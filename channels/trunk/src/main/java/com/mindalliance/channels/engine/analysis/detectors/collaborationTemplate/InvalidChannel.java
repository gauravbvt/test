/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

public class InvalidChannel extends AbstractIssueDetector {

    @Override
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Channelable channelable = (Channelable) modelObject;
        List<Channel> channels = channelable.getEffectiveChannels();
        for ( Channel channel : channels ) {
            // Check for valid medium and if valid for valid address.
            TransmissionMedium medium = channel.getMedium();
            String problem;
            String remediation = "";
            if ( medium == null || medium.isUnknown() ) {
                problem = "Channel is missing a transmission medium.";
                remediation = "Provide a valid medium for the channel.";
            } else if ( medium.hasInvalidAddressPattern() ) {
                problem = " Medium " + medium.getName() + " has an invalid address pattern";
                remediation = "Fix the address pattern" + "\nor remove the address pattern from the definition of "
                              + medium.getName();
            } else {
                problem = channelable.validate( channel );
                if ( problem != null )
                    remediation = "Provide a correct address for " + medium + ".";
            }
            if ( problem != null ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, modelObject );
                issue.setDescription( channel.toString() + ": " + problem );
                issue.setRemediation( remediation );
                issue.setSeverity( getSeverity( channelable, communityService ) );
                issues.add( issue );
            }
        }
        return issues;
    }

    private static Level getSeverity( Channelable channelable, CommunityService communityService ) {
        QueryService queryService = communityService.getPlanService();
        if ( channelable instanceof Flow ) {
            Node target = ( (Flow) channelable ).getTarget();
            return target.isPart() ? queryService.computePartPriority( (Part) target ) : Level.Low;
        } else
            return Level.Low;
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Channelable;
    }

    @Override
    protected String getKindLabel() {
        return "Invalid channel definition";
    }

    @Override
    public String getTestedProperty() {
        return null;
    }
}
