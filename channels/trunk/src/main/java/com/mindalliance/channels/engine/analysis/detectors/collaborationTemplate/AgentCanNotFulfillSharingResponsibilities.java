package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Agent can not fulfill commitment(s) per flow.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/2/11
 * Time: 9:23 PM
 */
public class AgentCanNotFulfillSharingResponsibilities extends AbstractIssueDetector {

    public AgentCanNotFulfillSharingResponsibilities() {
    }

    @Override
    public List<Issue> detectIssues( final CommunityService communityService, Identifiable modelObject ) {
        final QueryService queryService = communityService.getPlanService();
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        if ( flow.isSharing() ) {
            Map<Actor, List<Commitment>> actorCommitments = findActorCommitments( queryService, flow );
            for ( Actor actor : actorCommitments.keySet() ) {
                Set<String> allProblems = new HashSet<String>();
                List<Commitment> commitments = actorCommitments.get( actor );
                if ( !commitments.isEmpty() ) {
                    for ( Commitment commitment : commitments ) {
                        allProblems.addAll( getAnalyst().findRealizabilityProblems(
                                queryService.getPlan(),
                                commitment,
                                communityService ) );
                    }
                    if ( flow.isAll() && commitments.size() > 1 ) {
                        if ( !allProblems.isEmpty() ) {
                            Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, flow );
                            issue.setDescription( "\""
                                    + actor.getName()
                                    + "\" can not fulfill all commitments as required"
                                    + " because of these problems: "
                                    + ChannelsUtils.listToString( new ArrayList<String>( allProblems ), ", and " ) );
                            issue.setRemediation( "Make sure that all commitments of "
                                    + "\""
                                    + actor.getName()
                                    + "\" from this flow can be realized." ); // TODO - elaborate
                            issue.setSeverity( computeSharingFailureSeverity( queryService, flow ) );
                            issues.add( issue );
                        }
                    } else {
                        boolean noneRealizable = !CollectionUtils.exists(
                                commitments,
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        return getAnalyst().findRealizabilityProblems(
                                                queryService.getPlan(),
                                                (Commitment) object,
                                                communityService ).isEmpty();
                                    }
                                }
                        );
                        if ( noneRealizable ) {
                            Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, flow );
                            issue.setDescription( "\""
                                    + actor.getName()
                                    + "\" can not fulfill any commitment"
                                    + " because of problems such as: "
                                    + ChannelsUtils.listToString( new ArrayList<String>( allProblems ), ", and " ) );
                            issue.setRemediation( "Make sure that at least one commitment of "
                                    + "\""
                                    + actor.getName()
                                    + "\" from this flow can be realized." ); // TODO - elaborate
                            issue.setSeverity( computeSharingFailureSeverity( queryService, flow ) );
                            issues.add( issue );
                        }
                    }
                }
            }
        }
        return issues;
    }


    private Map<Actor, List<Commitment>> findActorCommitments( QueryService queryService, Flow flow ) {
        Map<Actor, List<Commitment>> actorCommitments = new HashMap<Actor, List<Commitment>>();
        List<Commitment> commitments = queryService.findAllCommitments( flow );
        for ( Commitment commitment : commitments ) {
            Actor actor = commitment.getCommitter().getActor();
            List<Commitment> list = actorCommitments.get( actor );
            if ( list == null ) {
                list = new ArrayList<Commitment>();
                actorCommitments.put( actor, list );
            }
            list.add( commitment );
        }
        return actorCommitments;
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Agent can not fulfill communication commitments";
    }
}
