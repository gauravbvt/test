package com.mindalliance.playbook.model;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * A snail mail address.
 */
@Entity
@Indexed
public class AddressMedium extends Medium {

    private static final long serialVersionUID = -7031507461651310787L;

    private Address address;

    public AddressMedium() {
    }

    public AddressMedium( String type, Address address ) {
        this( null, type, address );
    }
    
    private AddressMedium( Contact contact, String type, Address address ) {
        super( contact, type );

        if ( address == null )
            throw new IllegalArgumentException();
        this.address = address;
    }

    public AddressMedium( Contact contact, AddressMedium medium ) {
        this( contact, medium.getType(), medium.getAddress() );
    }

    @Override
    public MediumType getMediumType() {
        return MediumType.ADDRESS;
    }

    @IndexedEmbedded
    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public String getCssClass() {
        return "m-address";
    }

    @Override
    @Transient
    public String getDescription() {
        return "meeting at " + address;
    }

    @Override
    public String getDescription( Contact contact, boolean incoming ) {
        return "Meet " + contact.getFullName() + " at " + address;
    }

    @Override
    public String getDescription( boolean me ) {
        return "Meeting " + ( me ? "me" : "you" ) + " at " + address;
    }

    @Override
    public String getActionUrl() {
        try {
            return "http://maps.google.com/maps?q=" + URLEncoder.encode( address.toString(), "UTF8" );
        } catch ( UnsupportedEncodingException e ) {
            return null;
        }
    }
}
