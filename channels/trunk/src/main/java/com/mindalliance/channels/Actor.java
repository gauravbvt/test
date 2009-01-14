package com.mindalliance.channels;

import com.mindalliance.channels.analysis.profiling.Play;
import com.mindalliance.channels.analysis.profiling.Resource;
import com.mindalliance.channels.pages.Project;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

/**
 * Someone or something playing a part in a scenario.
 */
public class Actor extends ModelObject implements Resourceable {

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
