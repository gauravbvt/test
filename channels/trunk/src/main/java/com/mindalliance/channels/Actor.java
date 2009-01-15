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
     * @return a new or existing actor
     */
    public static Actor named( String name ) {
        Dao dao = Project.getProject().getDao();
        return dao.findOrMakeActor( name );
    }

}
