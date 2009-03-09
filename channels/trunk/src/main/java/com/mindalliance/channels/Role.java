package com.mindalliance.channels;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * A generic role.
 */
@Entity
public class Role extends ModelObject {

    /** The undefined role. */
    public static final Role UNKNOWN = new Role( "(unknown)" );

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
     * {@inheritDoc}
     */
    @Transient @Override
    public boolean isEntity() {
        return true;
    }

}
