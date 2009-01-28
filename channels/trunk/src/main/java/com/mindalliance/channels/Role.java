package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;

/**
 * A generic role.
 */
public class Role extends ModelObject implements ModelEntity {

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
        Dao dao = Project.dao();
        return dao.findOrMakeRole( name );
    }

}
