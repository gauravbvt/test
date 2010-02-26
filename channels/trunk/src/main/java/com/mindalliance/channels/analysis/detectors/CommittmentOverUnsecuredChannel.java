package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Channelable;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Severity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * A commitment can not be carried out over a channel with required security.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 9, 2009
 * Time: 11:57:53 AM
 */
public class CommittmentOverUnsecuredChannel extends AbstractIssueDetector {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Flow flow = (Flow) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        if ( flow.isSharing() && flow.isClassified() ) {
            final QueryService queryService = getQueryService();
            List<Classification> classifications = flow.getClassifications();
            for ( final Commitment commitment : queryService.findAllCommitments( flow ) ) {
                // Verify that for each channel in the flow,
                // the contacted entity in a commitment has a channel
                // that has a security classification equal or higher than the most classified
                // EOI in the flow.
                Channelable contacted = contactedEntity( commitment );
                for ( final Channel flowChannel : flow.getEffectiveChannels() ) {
                    List<Channel> contactChannels = (List<Channel>) CollectionUtils.select(
                            contacted.getEffectiveChannels(),
                            new Predicate() {
                                public boolean evaluate( Object object ) {
                                    return ( (Channel) object ).getMedium().narrowsOrEquals( flowChannel.getMedium() );
                                }
                            }
                    );
                    for ( Channel contactChannel : contactChannels ) {
                        if ( !contactChannel.isSecuredFor( classifications ) ) {
                            Issue issue = makeIssue( Issue.ROBUSTNESS, flow );
                            issue.setDescription( contacted.getName()
                                    + " can not be contacted via "
                                    + flowChannel.getMedium().getName()
                                    + " with the security required by the information's classification"
                            );
                            issue.setRemediation( "Make sure that "
                                    + contacted.getName()
                                    + " can be contacted securely via "
                                    + flowChannel.getMedium().getName()
                                    + "\nor change the classification of the elements of \""
                                    + flow.getName()
                                    + "\""
                            );
                            issue.setSeverity( Severity.Major );
                            issues.add( issue );
                        }
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
    protected String getLabel() {
        return "Commitment over unsecured channel";
    }

    private Channelable contactedEntity( Commitment commitment ) {
        if ( commitment.getSharing().isAskedFor() ) {
            return commitment.getCommitter().getChannelable();
        } else {
            return commitment.getBeneficiary().getChannelable();
        }
    }

}
