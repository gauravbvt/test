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
public class Actor extends ModelObject implements Player, Resourceable {

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
     * {@inheritDoc}
     */
    public List<Play> findAllPlays() {
        return Project.getProject().findAllPlaysFor( this );
    }

    /**
     * Find all implied resources within the current project.
     *
     * @return a list of resources
     */
    public List<Resource> findAllResources() {
        return Project.getProject().findAllResourcesFor( this );
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
