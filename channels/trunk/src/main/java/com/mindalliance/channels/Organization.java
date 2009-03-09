package com.mindalliance.channels;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.text.MessageFormat;

/**
 * A company, agency, social club, etc.
 */
@Entity
public class Organization extends ModelObject {

    /** The unknown organization. */
    public static final Organization UNKNOWN = new Organization( "(unknown)" );

    /** Parent organization. May be null. */
    private Organization parent;
    /**
     * The primary location of the organization
     */
    private Place location;

    public Organization() {
    }

    /**
     * Utility constructor for tests.
     *
     * @param name the name of the new object
     */
    public Organization( String name ) {
        super( name );
    }

    /**
     * {@inheritDoc}
     */
    @Transient @Override
    public boolean isEntity() {
        return true;
    }

    @ManyToOne( cascade = CascadeType.PERSIST )
    public Organization getParent() {
        return parent;
    }

    public void setParent( Organization parent ) {
        this.parent = parent;
    }

    @ManyToOne( cascade = CascadeType.PERSIST )
    public Place getLocation() {
        return location;
    }

    public void setLocation( Place location ) {
        this.location = location;
    }

    /**
     * Whether this organization has for parent a given organization (transitive)
     * @param organization an organization
     * @return a boolean
     */
    public boolean isWithin( Organization organization ) {
        return parent != null && ( parent == organization || parent.isWithin( organization ) );

    }

    /**
     * Whether this is the same or within a given organization
     * @param organization an organization
     * @return a boolean
     */
    public boolean isSameOrWithin( Organization organization ) {
        return this == organization || isWithin( organization );
    }

    /**
     * A string that shows line of parent organizations
     * @return a String
     */
    public String parentage() {
        String parentage = parent == null ? "" : parent.getName() + "," + parent.parentage();
        return parentage.endsWith( "," ) ? parentage.substring( 0, parentage.length() - 1 )
                                         : parentage;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return parent == null ? getName()
                              : MessageFormat.format( "{0} - {1}", parent.toString(), getName() );
    }
}

