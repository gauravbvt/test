package com.mindalliance.playbook.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * A snail mail address.
 */
@Entity
@DiscriminatorValue( "1" )
public class AddressMedium extends Medium {

    private static final long serialVersionUID = -7031507461651310787L;

    private Address address;

    public AddressMedium() {
    }

    public AddressMedium( Contact contact, String type, Address address ) {
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

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null || !getClass().isAssignableFrom( obj.getClass() ) )
            return false;

        AddressMedium other = getClass().cast( obj );

        return getType().equals( other.getType() )
            && address.equals( other.getAddress() );
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + address.hashCode();
        return result;
    }
}
