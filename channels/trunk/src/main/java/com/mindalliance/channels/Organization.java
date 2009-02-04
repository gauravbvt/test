package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * A company, agency, social club, etc.
 */
@Entity
public class Organization extends ModelObject {

    public Organization() {
    }

    // TODO Add property: parent

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

}

