package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.engine.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Channelable has a redundant channel (e.g. phone and cell).
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 7, 2010
 * Time: 10:33:38 PM
 */
public class RedundantChannel extends AbstractIssueDetector {

    public RedundantChannel() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Channelable channelable = (Channelable) modelObject;
        List<Channel> channels = channelable.getEffectiveChannels();
        Place locale = queryService.getPlan().getLocale();
        for ( Channel channel : channels ) {
            for ( Channel other : channels ) {
                if ( !channel.equals( other ) ) {
                    if ( channel.getAddress().isEmpty() &&
                            other.getAddress().isEmpty()
                            && channel.getMedium().narrowsOrEquals( other.getMedium(), locale ) ) {
                        Issue issue = makeIssue( queryService, Issue.VALIDITY, (ModelObject) channelable );
                        issue.setDescription( "Channel \""
                                + channel.getLabel()
                                + "\" is a special case of \""
                                + other.getLabel()
                                + "\" and is thus redundant." );
                        issue.setRemediation( "Remove \""
                                + channel.getLabel()
                                + "\"\nor remove channel \""
                                + other.getLabel()
                                + "\"." );
                        issue.setSeverity( Level.Low );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Channelable;
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
    protected String getKindLabel() {
        return "Redundant channel";
    }
}
