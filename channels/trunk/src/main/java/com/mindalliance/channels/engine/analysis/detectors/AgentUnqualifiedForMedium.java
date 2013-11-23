package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Agent has commitment from flow with medium it is not qualified for.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 8/26/11
 * Time: 11:58 AM
 */
public class AgentUnqualifiedForMedium extends AbstractIssueDetector {

    public AgentUnqualifiedForMedium() {
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        List<TransmissionMedium> qualifiedMedia = (List<TransmissionMedium>) CollectionUtils.select(
                CollectionUtils.collect(
                        flow.getChannels(),
                        new Transformer() {
                            @Override
                            public Object transform( Object input ) {
                                return ( (Channel) input ).getMedium();
                            }
                        }
                ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TransmissionMedium) object ).getQualification() != null;
                    }
                } );
        if ( !qualifiedMedia.isEmpty() ) {
            List<Commitment> commitments = communityService.getPlanService().findAllCommitments( flow );
            Set<Actor> unqualified = new HashSet<Actor>();
            for ( Commitment commitment : commitments ) {
                checkQualification(
                        commitment.getCommitter().getActor(),
                        qualifiedMedia,
                        issues,
                        flow,
                        true,
                        unqualified,
                        communityService );
            }
            unqualified = new HashSet<Actor>();
            for ( Commitment commitment : commitments ) {
                checkQualification(
                        commitment.getBeneficiary().getActor(),
                        qualifiedMedia,
                        issues,
                        flow,
                        false,
                        unqualified,
                        communityService );
            }
        }
        return issues;
    }

    private void checkQualification( Actor actor, List<TransmissionMedium> qualifiedMedia, List<Issue> issues, Flow flow,
                                     boolean isCommitter, Set<Actor> unqualified, CommunityService communityService ) {
        Place planLocale = communityService.getPlanService().getPlanLocale();
        for ( TransmissionMedium medium : qualifiedMedia ) {
            Actor qualification = medium.getQualification();
            if ( !actor.narrowsOrEquals( qualification, planLocale ) && !unqualified.contains( actor ) ) {
                Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, flow );
                issue.setDescription( "Agent \"" + actor.getName() + "\" is not "
                        + ( ChannelsUtils.startsWithVowel( qualification.getName() ) ? "an " : "a " )
                        + qualification.getName().toLowerCase()
                        + " and is thus not qualified to "
                        + ( isCommitter ? "transmit " : "receive " )
                        + "over "
                        + medium.getName().toLowerCase()
                        + "." );
                issue.setRemediation( "Make agent \"" + actor.getName() + "\" "
                        + ( ChannelsUtils.startsWithVowel( qualification.getName() ) ? "an " : "a " )
                        + qualification.getName().toLowerCase()
                        + "\nor remove the qualification requirement from medium \""
                        + medium.getName().toLowerCase()
                        + "\""
                        + "\nor remove \""
                        + medium.getName().toLowerCase()
                        + "\" from the flow's channels"
                        + "\nor modify the definition of the "
                        + ( isCommitter ? "source " : "target " )
                        + "task so that the unqualified \"" + actor.getName() + "\" is not assigned to it." );
                issue.setSeverity( computeSharingFailureSeverity( communityService.getPlanService(), flow ) );
                issues.add( issue );
                unqualified.add( actor );
            }
        }
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow && ( (Flow) modelObject ).isSharing();
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Agent with communication commitment is not qualified to use a transmission medium";
    }
}
