package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;

/**
 * Someone or something playing a part in a scenario.
 */
public class Actor extends ModelObject implements Entity {

    public Actor() {
    }

    /**
     * Utility constructor for tests.
     *
     * @param name the name of the new object
     */
    public Actor( String name ) {
        super( name );
    }

    /**
     * Find or create an actor by name
     *
     * @param name String a given name
     * @return a new or existing actor, or null is name is null or empty
     */
    public static Actor named( String name ) {
        if ( name == null || name.isEmpty() ) return null;
        Dao dao = Project.dao();
        return dao.findOrMakeActor( name );
    }

}
