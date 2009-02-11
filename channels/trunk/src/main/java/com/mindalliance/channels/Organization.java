package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.CascadeType;

/**
 * A company, agency, social club, etc.
 */
@Entity
public class Organization extends ModelObject {

    /** Parent organization. May be null. */
    private Organization parent;

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
     * Find or create an organization by name
     *
     * @param name String a given name
     * @return a new or existing organization, or null is name is null or empty
     */
    public static Organization named( String name ) {
        if ( name == null || name.isEmpty() ) return null;
        return Project.service().findOrCreate( Organization.class, name );
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

    /**
     * Whether this organization has for parent a given organization (transitive)
     * @param organization an organization
     * @return a boolean
     */
    public boolean isWithin( Organization organization ) {
        if ( parent == null )
            return false;
        else
            return parent == organization || parent.isWithin( organization );

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
        String parentage = (parent == null) ?
                "" : parent.getName() + "," + parent.parentage();
        return (parentage.endsWith( "," )) ?
                parentage.substring( 0, parentage.length() - 1 ) : parentage;
    }
}

