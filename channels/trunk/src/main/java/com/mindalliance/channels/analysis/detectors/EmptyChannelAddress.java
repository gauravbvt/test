package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Channelable;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * Required channel address is empty.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/6/11
 * Time: 10:02 PM
 */
public class EmptyChannelAddress extends AbstractIssueDetector {

    public EmptyChannelAddress() {
    }

    @Override
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Channelable channelable = (Channelable) modelObject;
        List<Channel> channels = channelable.getEffectiveChannels();
        for ( Channel channel : channels ) {
            String problem;
            String remediation = "";
            if ( channel.getAddress().isEmpty() ) {
                if ( channel.getMedium().requiresAddress()
                        && !( modelObject.isEntity() && ( (ModelEntity) modelObject ).isType() )
                        && !( modelObject instanceof Actor && ( (Actor) modelObject ).isPlaceHolder() ) ) {
                    problem = "The " + channel.getMedium().getName() + " channel's address is required but empty.";
                    remediation = "Enter a valid address.";
                    Issue issue = makeIssue( Issue.COMPLETENESS, modelObject );
                    issue.setDescription( channel.toString() + ": " + problem );
                    issue.setRemediation( remediation );
                    issue.setSeverity( getSeverity( channelable ) );
                    issues.add( issue );

                }
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
                return Level.Medium;
            }
        } else {
            return Level.Medium;
        }
    }


    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Channelable;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Required channel address is empty";
    }
}
