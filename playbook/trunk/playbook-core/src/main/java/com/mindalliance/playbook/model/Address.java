package com.mindalliance.playbook.model;

import org.hibernate.search.annotations.Field;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Mailing address of a contact.
 */
@Embeddable
public class Address implements Serializable {

    private static final long serialVersionUID = 6414432307685697213L;

    private String street;

    private String locality;

    private String region;

    private String country;
    
    private String postalCode;
    
    private String poBox;

    public Address() {
    }

    /**
     * Build an address string
     * @param unparsed an address
     */
    public Address( String unparsed ) {
        // TODO Parse more intelligently...
        this.region = unparsed;
    }

    @Field
    public String getStreet() {
        return street;
    }

    public void setStreet( String street ) {
        this.street = street;
    }

    @Field
    public String getLocality() {
        return locality;
    }

    public void setLocality( String locality ) {
        this.locality = locality;
    }

    @Field
    public String getRegion() {
        return region;
    }

    public void setRegion( String region ) {
        this.region = region;
    }

    @Field
    public String getCountry() {
        return country;
    }

    public void setCountry( String country ) {
        this.country = country;
    }

    @Field
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode( String postalCode ) {
        this.postalCode = postalCode;
    }

    public String getPoBox() {
        return poBox;
    }

    public void setPoBox( String poBox ) {
        this.poBox = poBox;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
 
        if ( street != null && !street.isEmpty() )
            sb.append( street ).append( ", " );
        if ( locality != null && !locality.isEmpty() )
            sb.append( locality ).append( ' ' );
        if ( region != null && !region.isEmpty() )
            sb.append( region );

        if ( postalCode != null && !postalCode.isEmpty() )
            sb.append( ' ' ).append( postalCode );

        if ( country != null && !country.isEmpty() )
            sb.append( ' ' ).append( country );

        if ( poBox != null && !poBox.isEmpty() )
            sb.append( ", PO Box " ).append( poBox );
        
        return sb.toString();
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null || getClass() != obj.getClass() )
            return false;

        Address address = (Address) obj;

        if ( country != null ? !country.equals( address.getCountry() ) : address.getCountry() != null )
            return false;
        if ( locality != null ? !locality.equals( address.getLocality() ) : address.getLocality() != null )
            return false;
        if ( poBox != null ? !poBox.equals( address.getPoBox() ) : address.getPoBox() != null )
            return false;
        if ( postalCode != null ? !postalCode.equals( address.getPostalCode() ) : address.getPostalCode() != null )
            return false;
        if ( region != null ? !region.equals( address.getRegion() ) : address.getRegion() != null )
            return false;
        if ( street != null ? !street.equals( address.getStreet() ) : address.getStreet() != null )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = street != null ? street.hashCode() : 0;
        result = 31 * result + ( locality != null ? locality.hashCode() : 0 );
        result = 31 * result + ( region != null ? region.hashCode() : 0 );
        result = 31 * result + ( country != null ? country.hashCode() : 0 );
        result = 31 * result + ( postalCode != null ? postalCode.hashCode() : 0 );
        result = 31 * result + ( poBox != null ? poBox.hashCode() : 0 );
        return result;
    }
}
