package com.mindalliance.channels.model;

import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An agreement to share information for some purpose.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 28, 2009
 * Time: 10:59:27 AM
 */
public class Agreement implements Serializable {

    /**
     * Organization that is the beneficiary of the agreement.
     */
    private Organization beneficiary;
    /**
     * Name of information.
     */
    private String information = "";
    /**
     * Elements of information.
     */
    private List<ElementOfInformation> eois = new ArrayList<ElementOfInformation>();
    /**
     * Task for which info can be used.
     */
    private String usage = "";

    public Agreement() {
    }

    /**
     * Whether a commitment implies an agreement.
     * @param commitment a commitment
     * @return a boolean
     */
    public static boolean impliesAnAgreement( Commitment commitment ) {
        return !commitment.getBeneficiary().getOrganization().isUnknown()
                && !commitment.getSharing().getName().isEmpty();
    }

    /**
     * Create agreement implied from commitment.
     *
     * @param commitment a commitment
     * @return an agreement
     */
    public static Agreement from( Commitment commitment ) {
        assert impliesAnAgreement( commitment );
        Agreement agreement = new Agreement();
        agreement.setBeneficiary( commitment.getBeneficiary().getOrganization() );
        agreement.setInformation( commitment.getSharing().getName() );
        agreement.setEois( commitment.getSharing().copyEois() );
        agreement.setUsage( commitment.getBeneficiary().getPart().getTask() );
        return agreement;
    }

    public Organization getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary( Organization beneficiary ) {
        this.beneficiary = beneficiary;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation( String information ) {
        this.information = information;
    }

    public List<ElementOfInformation> getEois() {
        return eois;
    }

    public void setEois( List<ElementOfInformation> eois ) {
        this.eois = eois;
    }

    /**
     * Add element of information.
     *
     * @param eoi an element of information
     */
    public void addEoi( ElementOfInformation eoi ) {
        if ( !eois.contains( eoi ) ) eois.add( eoi );
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage( String usage ) {
        this.usage = usage;
    }

    /**
     * Gets a summary of the agreement by an organization.
     *
     * @param organization an organization
     * @return a string
     */
    public String getSummary( Organization organization ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Agreement by " );
        sb.append( organization.getName() );
        sb.append( " with " );
        sb.append( beneficiary.getName() );
        sb.append( " to share " );
        if ( information.isEmpty() ) {
            sb.append( "everything" );
        } else {
            sb.append( information );
            sb.append( '[' );
            sb.append( getEOIsString() );
            sb.append( ']' );
        }
        if ( usage.isEmpty() ) {
            sb.append( " for any use" );
        } else {
            sb.append( " for " );
            sb.append( usage );
        }
        return sb.toString();
    }

    /**
     * Get simple string representation of eois.
     *
     * @return a string
     */
    public String getEOIsString() {
        StringBuilder sb = new StringBuilder();
        Iterator<ElementOfInformation> iter = getEois().iterator();
        while ( iter.hasNext() ) {
            sb.append( iter.next().getLabel() );
            if ( iter.hasNext() ) sb.append( ',' );
        }
        return sb.toString();

    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object object ) {
        if ( object instanceof Agreement ) {
            Agreement other = (Agreement) object;
            return getBeneficiary().equals( other.getBeneficiary() )
                    && getInformation().equals( other.getInformation() )
                    && getUsage().equals( other.getUsage() )
                    && CollectionUtils.isEqualCollection( getEois(), other.getEois() );
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + getBeneficiary().hashCode();
        hash = hash * 31 + getInformation().hashCode();
        hash = hash * 31 + getUsage().hashCode();
        for ( ElementOfInformation eoi : getEois() ) {
            hash = hash * 31 + eoi.hashCode();
        }
        return hash;
    }


}
