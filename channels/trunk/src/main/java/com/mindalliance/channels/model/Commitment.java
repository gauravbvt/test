package com.mindalliance.channels.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.List;

/**
 * A commitment to share information.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 28, 2009
 * Time: 10:21:32 AM
 */
public class Commitment implements Serializable {
    /**
     * Assignment committed.
     */
    private Assignment committer;
    /**
     * Beneficiary assignment.
     */
    private Assignment beneficiary;
    /**
     * Flow that implies the commitment.
     */
    private Flow sharing;

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

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( committer.toString() );
        sb.append( " commits to sharing \"" );
        sb.append( sharing.getName() );
        sb.append( "\" with " );
        sb.append( beneficiary.toString() );
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object object ) {
        if ( !( object instanceof Commitment ) ) return false;
        Commitment other = (Commitment) object;
        return committer.equals( other.getCommitter() )
                && beneficiary.equals( other.getBeneficiary() )
                && sharing.equals( other.getSharing() );
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + committer.hashCode();
        hash = hash * 31 + beneficiary.hashCode();
        hash = hash * 31 + sharing.hashCode();
        return hash;
    }

    /**
     * Whether the commitment survives the classification test.
     *
     * @return a boolean
     */
    public boolean passesClearanceTest() {
        List<ElementOfInformation> eois = sharing.getEois();
        if ( eois.isEmpty() ) return true;
        List<ElementOfInformation> clearedEOIs = getClearedEOIs();
        return !clearedEOIs.isEmpty();
    }

    /**
     * The sharing flow's EOIs that are cleared for the beneficiary actor.
     *
     * @return a list of EOIs
     */
    @SuppressWarnings( "unchecked" )
    public List<ElementOfInformation> getClearedEOIs() {
        return (List<ElementOfInformation>) CollectionUtils.select(
                sharing.getEois(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (ElementOfInformation) obj ).isClearedFor( beneficiary.getActor() );
                    }
                }
        );
    }
}
