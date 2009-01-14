package com.mindalliance.channels;

import com.mindalliance.channels.analysis.profiling.Play;
import com.mindalliance.channels.analysis.profiling.Resource;
import com.mindalliance.channels.pages.Project;

import java.util.List;
import java.util.ArrayList;

/**
 * A company, agency, social club, etc.
 */
public class Organization extends ModelObject implements Player, Resourceable {

    public Organization() {
    }

    // TODO Add properties: mission, parent

    /**
     * Utility constructor for tests.
     *
     * @param name the name of the new object
     */
    public Organization( String name ) {
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
}
