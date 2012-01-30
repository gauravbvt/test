package com.mindalliance.playbook.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Generic medium.
 */
@Entity
@DiscriminatorValue( "0" )
public class OtherMedium extends Medium {

    private static final long serialVersionUID = 4702889067635290869L;

    private String address;

    public OtherMedium() {
    }

    public OtherMedium( Contact contact, String type, String address ) {
        super( contact, type );

        if ( address == null )
            throw new IllegalArgumentException();
        this.address = address;
    }

    public OtherMedium( Contact contact, OtherMedium medium ) {
        this( contact, medium.getType(), medium.getAddress() );
    }

    @Override
    public MediumType getMediumType() {
        return MediumType.OTHER;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null || !getClass().isAssignableFrom( obj.getClass() ) )
            return false;

        OtherMedium other = getClass().cast( obj );

        return getType().equals( other.getType() )
            && address.equals( other.getAddress() );
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + address.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
                
        sb.append( getType() ).append( ": " );
        sb.append( address );

        return sb.toString();
    }
}
