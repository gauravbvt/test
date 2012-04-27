// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.model;

import javax.persistence.Entity;

/**
 * A Skype medium.
 */
@Entity
public class SkypeMedium extends GenericMedium {

    public SkypeMedium() {
    }

    public SkypeMedium( Contact contact, SkypeMedium medium ) {
        super( contact, medium );
    }

    public SkypeMedium( String address ) {
        super( null, null, address );
    }

    @Override
    public MediumType getMediumType() {
        return MediumType.SKYPE;
    }

    @Override
    public String getCssClass() {
        return "m-skype";
    }

    @Override
    public String getDescription( boolean me ) {
        return "Calling " + ( me ? "me" : "you" ) + " on Skype";
    }

    @Override
    public String getDescription( Contact contact, boolean incoming ) {
        return incoming ? "Receiving a call from " + contact + " on Skype" 
                        : "Call " + contact + " on Skype";
    }

    @Override
    public String getActionUrl() {
        return "skype:" + getAddress() + "?call";
    }
}
