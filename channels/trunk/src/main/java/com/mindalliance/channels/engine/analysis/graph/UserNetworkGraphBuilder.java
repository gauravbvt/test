package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/4/13
 * Time: 10:42 AM
 */
public class UserNetworkGraphBuilder implements GraphBuilder<ChannelsUser, UserCommitmentRelationship> {

    private CommunityService communityService;


    public UserNetworkGraphBuilder( CommunityService communityService ) {
        this.communityService = communityService;
    }


    @Override
    public DirectedGraph<ChannelsUser, UserCommitmentRelationship> buildDirectedGraph() {
        DirectedGraph<ChannelsUser, UserCommitmentRelationship> digraph =
                new DirectedMultigraph<ChannelsUser, UserCommitmentRelationship>(
                        new EdgeFactory<ChannelsUser, UserCommitmentRelationship>() {
                            /**
                             * {@inheritDoc}
                             */
                            public UserCommitmentRelationship createEdge(
                                    ChannelsUser user,
                                    ChannelsUser otherUser ) {
                                return new UserCommitmentRelationship( user, otherUser );
                            }

                        } );
        populateGraph( digraph );
        return digraph;
    }

    private void populateGraph( DirectedGraph<ChannelsUser, UserCommitmentRelationship> digraph ) {
        ParticipationManager participationManager = communityService.getParticipationManager();
        List<ChannelsUser> allParticipatingUsers =
                participationManager.findAllActivelyParticipatingUsers( communityService );
        for ( ChannelsUser user : allParticipatingUsers ) {
            digraph.addVertex( user );
        }
        for ( CommunityCommitment communityCommitment : communityService.getAllCommitments( true )) {
            Agent committer = communityCommitment.getCommitter().getAgent();
            Agent beneficiary = communityCommitment.getBeneficiary().getAgent();
            for ( UserRecord committerUserRecord
                    : participationManager.findUsersActivelyParticipatingAs( committer, communityService ) ) {
                for (UserRecord beneficiaryUserRecord
                        : participationManager.findUsersActivelyParticipatingAs( beneficiary, communityService ) ) {
                    ChannelsUser committerUser = new ChannelsUser( committerUserRecord );
                    ChannelsUser beneficiaryUser = new ChannelsUser( beneficiaryUserRecord );
                    UserCommitmentRelationship userRel = new UserCommitmentRelationship(
                            committerUser,
                            beneficiaryUser,
                            communityCommitment );
                    digraph.addEdge( committerUser, beneficiaryUser, userRel );
                }
            }
        }
    }
}
