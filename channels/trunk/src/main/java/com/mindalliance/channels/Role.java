package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;

/**
 * A generic role.
 */
public class Role extends ModelObject {

    public Role() {
    }

    /**
     * Utility constructor for tests.
     * @param name the name of the new object
     */
    public Role( String name ) {
        super( name );
    }

    public static Role named ( String name ) {
        Dao dao = Project.getProject().getDao();
        return dao.findOrMakeRole( name );
    }
}
