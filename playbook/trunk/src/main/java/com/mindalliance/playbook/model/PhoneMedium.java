// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.model;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;

/**
 * A phone number based medium.
 */
@Entity
@Indexed
public class PhoneMedium extends GenericMedium {

    private static final long serialVersionUID = -2142448575740408363L;

    public PhoneMedium() {
    }

    public PhoneMedium( Contact contact, PhoneMedium medium ) {
        super( contact, medium );
    }

    public PhoneMedium( String type, String phoneNumber ) {
        super( null, type, phoneNumber );
    }

    @Override
    public MediumType getMediumType() {
        return MediumType.PHONE;
    }

    @Override
    public String getCssClass() {
        return "m-phone";
    }

    @Override
    public String getActionUrl() {
        StringBuilder builder = new StringBuilder();
        builder.append( "tel:" );
        String address = getAddress();
        for ( int i = 0 ; i < address.length(); i++ ) {
            char ch = address.charAt( i );
            if ( Character.isDigit( ch ) )
                builder.append( ch );
        }
        
        return builder.toString();
    }

    @Override
    public String getDescription() {
        return "dialing " + getAddress();
    }

    @Override
    public String getDescription( Contact contact, boolean incoming ) {
        return incoming ? "Receiving a call from " + contact + " at " + getAddress()
                        : "Call " + contact + " at " + getAddress();
    }

    @Override
    public String getDescription( boolean me ) {
        return "Calling " + ( me ? "me" : "you" ) + " at " + getAddress();
    }
}
