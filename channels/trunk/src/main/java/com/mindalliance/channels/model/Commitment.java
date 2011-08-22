package com.mindalliance.channels.model;

import java.io.Serializable;

/**
 * A commitment to share information. Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary and
 * Confidential. User: jf Date: Oct 28, 2009 Time: 10:21:32 AM
 */
public class Commitment implements Serializable {

    /** Assignment committed. */
    private final Assignment committer;

    /** Beneficiary assignment. */
    private final Assignment beneficiary;

    /** Flow that implies the commitment. */
    private final Flow sharing;

    public Commitment( Assignment committer, Assignment beneficiary, Flow flow ) {
        this.committer = committer;
        this.beneficiary = beneficiary;
        sharing = flow;
    }

    public Assignment getCommitter() {
        return committer;
    }

    public Assignment getBeneficiary() {
        return beneficiary;
    }

    public Flow getSharing() {
        return sharing;
    }

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
        if ( !( obj instanceof Commitment ) )
            return false;

        Commitment other = (Commitment) obj;
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

    /**
     * Commitment crosses organization boundaries.
     * @return a boolean
     */
    public boolean isBetweenOrganizations() {
        return !committer.getOrganization().equals( beneficiary.getOrganization() );
    }

    public boolean isProhibited() {
        return sharing.isProhibited();
    }
}
