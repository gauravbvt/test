package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;

/**
 * Commitment relationship between users in a collaboration plan.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/4/13
 * Time: 10:38 AM
 */
public class UserCommitmentRelationship extends Relationship<ChannelsUser> {

    private ChannelsUser fromUser;
    private ChannelsUser toUser;
    private CommunityCommitment communityCommitment;

    public UserCommitmentRelationship( ChannelsUser fromUser,
                                       ChannelsUser toUser,
                                       CommunityCommitment communityCommitment ) {
        super( fromUser, toUser );
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.communityCommitment = communityCommitment;
    }

    public UserCommitmentRelationship( ChannelsUser fromUser,
                                       ChannelsUser toUser ) {
        super( fromUser, toUser );
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    public ChannelsUser getFromUser() {
        return fromUser;
    }

    public ChannelsUser getToUser() {
        return toUser;
    }

    public CommunityCommitment getCommunityCommitment() {
        return communityCommitment;
    }

    @Override
    public String toString() {
        return fromUser.getUsername() + "->"
                + toUser.getUsername()
                + " ["
                + communityCommitment.toString()
                + "]";
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof UserCommitmentRelationship ) {
           UserCommitmentRelationship other = (UserCommitmentRelationship)object;
            return fromUser.equals( other.getFromUser() )
                    && toUser.equals( other.getToUser() )
                    && communityCommitment.equals( other.getCommunityCommitment() );
        } else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + fromUser.hashCode();
        hash = hash * 31 + toUser.hashCode();
        hash = hash * 31 + communityCommitment.hashCode();
        return hash;
    }
}
