package com.mindalliance.channels.engine.analysis.detectors.collaborationPlan;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.DetectedIssue;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.UserCommitmentRelationship;
import com.mindalliance.channels.engine.analysis.graph.UserNetworkGraphBuilder;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.BlockCutpointGraph;
import org.jgrapht.graph.AsUndirectedGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User as single point of failure.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/4/13
 * Time: 8:58 AM
 */
public class UserIsSinglePointOfFailure extends AbstractIssueDetector {

    /**
     * Minimum out degree of a part that is a bottleneck and thus a single point of failure.
     */
    private static final int MINIMUM_DEGREE = 1;

    public UserIsSinglePointOfFailure() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof PlanCommunity;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        Set<ChannelsUser> spofUsers = detectSignificantCutpoints( communityService );
        // Found single points of failure?
        for ( ChannelsUser user : spofUsers ) {
                DetectedIssue issue = makeIssue( communityService, Issue.ROBUSTNESS, communityService.getPlanCommunity() );
                issue.setDescription( user.getFullName() + " appears to be a single point of failure." );
                issue.setRemediation( "If possible, request that other users participate the way "
                        + user.getFullName()
                        + " does"
                        + "\n or, if also possible, have other organizations participate with the same position(s) that are filled by "
                        + user.getFullName()
                        + "\n if none of the above are possible, ask that the collaboration model be upgraded allow users to second "
                        + user.getFullName()
                        + "."
                );
                issue.setSeverity( Level.High ); // todo - calculate based on failure impacts
                issues.add( issue );
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "User is a single point of failure";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }

    private static Set<ChannelsUser> detectSignificantCutpoints( CommunityService communityService ) {
        Set<ChannelsUser> cutpoints = new HashSet<ChannelsUser>();
        GraphBuilder<ChannelsUser, UserCommitmentRelationship> graphBuilder = new UserNetworkGraphBuilder( communityService );
        DirectedGraph<ChannelsUser, UserCommitmentRelationship> digraph = graphBuilder.buildDirectedGraph();
        if ( !digraph.edgeSet().isEmpty() ) {
            BlockCutpointGraph<ChannelsUser, UserCommitmentRelationship> bcg =
                    new BlockCutpointGraph<ChannelsUser, UserCommitmentRelationship>( new AsUndirectedGraph<ChannelsUser, UserCommitmentRelationship>( digraph ) );
            for ( ChannelsUser user : bcg.getCutpoints() )
                if ( digraph.outDegreeOf( user ) >= MINIMUM_DEGREE && digraph.inDegreeOf( user ) >= MINIMUM_DEGREE )
                    cutpoints.add( user );
        }
        return cutpoints;
    }

}
