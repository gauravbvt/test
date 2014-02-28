package com.mindalliance.channels.core.model;

import java.io.Serializable;

/**
 * A commitment to share information. Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary and
 * Confidential. User: jf Date: Oct 28, 2009 Time: 10:21:32 AM
 */
public class Commitment implements Serializable {

    /**
     * Assignment committed.
     */
    private final Assignment committer;

    /**
     * Beneficiary assignment.
     */
    private final Assignment beneficiary;

    /**
     * Flow that implies the commitment.
     */
    private final Flow sharing;

    public Commitment( Assignment committer, Assignment beneficiary, Flow flow ) {
        this.committer = committer;
        this.beneficiary = beneficiary;
        sharing = flow;
    }

    public Channelable getContactedEntity() {
        return sharing.isAskedFor() ? committer.getChannelable() : beneficiary.getChannelable();
    }

    /**
     * Whether a commitment could be met based on common language.
     *
     * @param collaborationModel the plan of concern
     * @return a boolean
     */
    public boolean isCommonLanguage( CollaborationModel collaborationModel ) {
        Actor committer = this.committer.getActor();
        Actor beneficiary = this.beneficiary.getActor();
        return committer.canSpeakWith( beneficiary, collaborationModel );
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
     *
     * @return a boolean
     */
    public boolean isBetweenUnrelatedOrganizations() {
        return !committer.getOrganization().getTopOrganization()
                .equals( beneficiary.getOrganization().getTopOrganization() );
    }

    public boolean isProhibited() {
        return sharing.isProhibited();
    }

    /**
     * Whether timing and event specifications (null means any) apply to the commitments event phase or context.
     *
     * @param timing     a phase timing or null
     * @param event      an event or null
     * @param planLocale a plan locale
     * @return a boolean
     */
    public boolean isInSituation( Phase.Timing timing, Event event, Place planLocale ) {
        return getSegment().isInSituation( timing, event, planLocale );
    }

    public Segment getSegment() {
        return getSharing().getSegment();
    }

    public boolean isInSegment( Segment seg ) {
        return getSharing().getSource().getSegment().equals( seg )
                || getSharing().getTarget().getSegment().equals( seg );
    }

    public boolean isToSelf() {
          return getSharing().isToSelf() || getCommitter().getActor().equals( getBeneficiary().getActor() );
    }
}
