package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * A generic role.
 */
@Entity
public class Role extends ModelObject {

    public Role() {
    }

    /**
     * Utility constructor for tests.
     *
     * @param name the name of the new object
     */
    public Role( String name ) {
        super( name );
    }

    /**
     * Find or create a role by name
     *
     * @param name String a given name
     * @return a new or existing role, or null is name is null or empty
     */
    public static Role named( String name ) {
        if ( name == null || name.isEmpty() ) return null;
        return Project.service().findOrCreate( Role.class, name );
    }

    /**
     * {@inheritDoc}
     */
    @Transient @Override
    public boolean isEntity() {
        return true;
    }

}
