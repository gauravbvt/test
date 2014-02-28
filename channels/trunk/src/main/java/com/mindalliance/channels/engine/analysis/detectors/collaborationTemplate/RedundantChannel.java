package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

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
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        List<Issue> issues = new ArrayList<Issue>();
        Channelable channelable = (Channelable) modelObject;
        List<Channel> channels = channelable.getEffectiveChannels();
        Place locale = queryService.getPlanLocale();
        for ( int i = 0; i < channels.size(); i++ ) {
            for ( int j = 0; j < channels.size(); j++ ) {
                if ( i != j ) {
                    Channel channel = channels.get( i );
                    Channel other = channels.get( j );
                    if ( channel.narrowsOrEquals( other, locale ) ) {
                        Issue issue = makeIssue( communityService, Issue.VALIDITY, (ModelObject) channelable );
                        issue.setDescription( "Channel \""
                                + channel.getLabel()
                                + "\" is a redundant with \""
                                + other.getLabel()
                                + "\"." );
                        issue.setRemediation( "Remove \""
                                + channel.getLabel()
                                + "\"\nor remove channel \""
                                + other.getLabel()
                                + "\nor set differently the format used in either." );
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
    public boolean appliesTo( Identifiable modelObject ) {
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
