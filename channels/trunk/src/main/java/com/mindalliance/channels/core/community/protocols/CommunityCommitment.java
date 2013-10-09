package com.mindalliance.channels.core.community.protocols;

import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;

import java.io.Serializable;

/**
 * A commitment in a plan community.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/3/13
 * Time: 3:57 PM
 */
public class CommunityCommitment implements Serializable {

    private CommunityAssignment committer;
    private CommunityAssignment beneficiary;
    private Flow sharing;

    public CommunityCommitment( CommunityAssignment committer, CommunityAssignment beneficiary, Flow sharing ) {
        this.beneficiary = beneficiary;
        this.committer = committer;
        this.sharing = sharing;
    }

    public CommunityAssignment getBeneficiary() {
        return beneficiary;
    }

    public CommunityAssignment getCommitter() {
        return committer;
    }

    public Flow getSharing() {
        return sharing;
    }

    public boolean isToSelf() {
        return committer.getAgent().equals( beneficiary.getAgent() );
    }


    //////////////////

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( committer.toString() );
        sb.append( " commits to sharing \"" );
        sb.append( sharing.getName() );
        sb.append( "\" with " );
        sb.append( beneficiary.toString() );
        return sb.toString();
    }

    public boolean equals( Object obj ) {
        if ( !( obj instanceof CommunityCommitment ) )
            return false;

        CommunityCommitment other = (CommunityCommitment) obj;
        return committer.equals( other.getCommitter() )
                && beneficiary.equals( other.getBeneficiary() )
                && sharing.equals( other.getSharing() );
    }

    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + committer.hashCode();
        hash = hash * 31 + beneficiary.hashCode();
        hash = hash * 31 + sharing.hashCode();
        return hash;
    }

    public boolean isInSituation( Phase.Timing timing, Event event, Place locale ) {
        return sharing.getSegment().isInSituation( timing, event, locale );
    }

    public Commitment getCommitment() {
        return new Commitment( getCommitter().getAssignment(),
                getBeneficiary().getAssignment(),
                sharing );
    }
}
