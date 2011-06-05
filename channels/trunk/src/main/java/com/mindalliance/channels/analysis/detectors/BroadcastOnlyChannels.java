package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * All channels for a flow are broadcast.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 7, 2010
 * Time: 3:44:38 PM
 */
public class BroadcastOnlyChannels extends AbstractIssueDetector {

    public BroadcastOnlyChannels() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        if ( flow.isSharing() ) {
            List<Channel> channels = flow.getEffectiveChannels();
            boolean allBroadcast =
                    !channels.isEmpty()
                            &&
                            !CollectionUtils.exists(
                                    channels,
                                    new Predicate() {
                                        public boolean evaluate( Object object ) {
                                            return !( (Channel) object ).getMedium().isBroadcast();
                                        }
                                    }
                            );
            if ( allBroadcast ) {
                Issue issue = makeIssue( Issue.ROBUSTNESS, flow );
                issue.setDescription( "There is no guarantee the information will be received because sharing is done only over broadcast channels." );
                issue.setRemediation( "Add an alternate, non-broadcast channel to the flow." );
                issue.setSeverity( getSharingFailureSeverity( flow ) );
                issues.add( issue );
            }
        }
        return issues;
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

    /**
     * {@inheritDoc}
     */
    protected String getKindLabel() {
        return "Sharing over broadcast channels only";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }
}
