// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.model;

import javax.persistence.Entity;

/**
 * A linkedIn connection information.
 */
@Entity
public class LinkedInMedium extends GenericMedium {

    private static final long serialVersionUID = -8589015332679716862L;

    private String profileUrl;

    private String displayName;

    public LinkedInMedium() {
    }

    public LinkedInMedium( Contact contact, LinkedInMedium medium ) {
        super( contact, medium );
        profileUrl = medium.getProfileUrl();
        displayName = medium.getDisplayName();
    }

    public LinkedInMedium( String linkedInId, String displayName, String profileUrl ) {
        super( null, null, linkedInId );
        this.displayName = displayName;
        this.profileUrl = profileUrl;
    }

    @Override
    public MediumType getMediumType() {
        return MediumType.LINKEDIN;
    }

    @Override
    public String getCssClass() {
        return "m-linkedin";
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl( String profileUrl ) {
        this.profileUrl = profileUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName( String displayName ) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public String getDescription( boolean me ) {
        return "Sending " + ( me ? "me" : "you" ) + " a message through LinkedIn";
    }

    @Override
    public String getDescription( Contact contact, boolean incoming ) {
        return incoming ? "Receiving a message from " + contact + " on LinkedIn"
                        : "Send " + contact + " a message through LinkedIn";
    }

    @Override
    public String getActionUrl() {
        return profileUrl;
    }
}
