// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.model;

import javax.persistence.Entity;

/**
 * Facebook contact coordinates.
 */
@Entity
public class FacebookMedium extends GenericMedium {

    private static final long serialVersionUID = -1365666384946168856L;

    private String name;

    public FacebookMedium() {
    }

    public FacebookMedium( Contact contact, FacebookMedium medium ) {
        super( contact, medium );
        name = medium.getName();
    }

    public FacebookMedium( String facebookId, String name ) {
        super( null, null, facebookId );
        this.name = name;
    }

    @Override
    public MediumType getMediumType() {
        return MediumType.FACEBOOK;
    }

    @Override
    public String getCssClass() {
        return "m-facebook";
    }
    
    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getDescription( boolean me ) {
        return "Contacting " + (me? "me" : "you") + " on Facebook";
    }

    @Override
    public String getDescription( Contact contact, boolean incoming ) {
        return incoming ? "Being contacted by " + contact + " on Facebook"
                        : "Contact " + contact + " on Facebook";
    }

    public String getName() {
        return name;
    }

    @Override
    public String getActionUrl() {
        return "http://www.facebook.com/messages/" + getAddress();
    }
}
