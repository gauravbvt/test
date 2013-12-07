package com.mindalliance.channels.core;

import com.mindalliance.channels.core.model.Identifiable;

import java.io.Serializable;

/**
 * An issue detection waiver for an identifiable.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/13
 * Time: 11:29 AM
 */
public class IssueDetectionWaiver implements Serializable {

    private String identifiableUid;
    private String identifiableName;
    private String identifiableTypeName;
    private String detector;

    public IssueDetectionWaiver() {
    }

    public IssueDetectionWaiver( Identifiable identifiable, String detector ) {
        identifiableUid = identifiable.getUid();
        identifiableName = identifiable.getName();
        identifiableTypeName = identifiable.getTypeName();
        this.detector = detector;
    }

    public boolean matches( Identifiable identifiable ) {
        return identifiableUid.equals( identifiable.getUid() )
                && identifiableTypeName.equals( identifiable.getTypeName() );
    }

    public String getDetector() {
        return detector;
    }

    public String getIdentifiableUid() {
        return identifiableUid;
    }

    public String getIdentifiableName() {
        return identifiableName;
    }

    public String getIdentifiableTypeName() {
        return identifiableTypeName;
    }

    public void setDetector( String detector ) {
        this.detector = detector;
    }

    public void setIdentifiableName( String identifiableName ) {
        this.identifiableName = identifiableName;
    }

    public void setIdentifiableTypeName( String identifiableTypeName ) {
        this.identifiableTypeName = identifiableTypeName;
    }

    public void setIdentifiableUid( String identifiableUid ) {
        this.identifiableUid = identifiableUid;
    }

    @Override
    public String toString() {
        return "Detection by "
                + detector
                + " waived for "
                + identifiableTypeName
                + " " + identifiableName
                + "[" + identifiableUid + "]";
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof IssueDetectionWaiver ) {
            IssueDetectionWaiver other = (IssueDetectionWaiver)object;
            return  identifiableTypeName.equals( other.getIdentifiableTypeName())
                    && identifiableUid.equals( other.getIdentifiableUid() )
                    && detector.equals( other.getDetector() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + identifiableUid.hashCode();
        hash = hash * 31 + identifiableTypeName.hashCode();
        hash = hash * 31 + detector.hashCode();
        return hash;
    }
}
