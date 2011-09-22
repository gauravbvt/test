package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;

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
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Channelable channelable = (Channelable) modelObject;
        List<Channel> channels = channelable.getEffectiveChannels();
        for ( Channel channel : channels ) {
            String problem;
            String remediation = "";
            if ( channel.getAddress().isEmpty() ) {
                if ( channel.getMedium().requiresAddress()
                        && !( modelObject instanceof Flow )
                        && !( modelObject.isEntity() && ( (ModelEntity) modelObject ).isType() )
                        && !( modelObject instanceof Actor && ( (Actor) modelObject ).isPlaceHolder() ) ) {
                    problem = "The " + channel.getMedium().getName() + " channel's address is required but empty.";
                    remediation = "Enter a valid address.";
                    Issue issue = makeIssue( queryService, Issue.COMPLETENESS, modelObject );
                    issue.setDescription( channel.toString() + ": " + problem );
                    issue.setRemediation( remediation );
                    issue.setSeverity( getSeverity( channelable, queryService ) );
                    issues.add( issue );

                }
            }
        }
        return issues;
    }

    private Level getSeverity( Channelable channelable, QueryService queryService ) {
        if ( channelable instanceof Flow ) {
            Flow flow = (Flow) channelable;
            return flow.getTarget().isPart() ?
                   queryService.computePartPriority( (Part) flow.getTarget() ) :
                   Level.Medium;
        } else
            return Level.Medium;
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
