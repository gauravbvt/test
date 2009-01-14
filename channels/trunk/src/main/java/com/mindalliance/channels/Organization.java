package com.mindalliance.channels;

import com.mindalliance.channels.analysis.profiling.Play;
import com.mindalliance.channels.analysis.profiling.Resource;
import com.mindalliance.channels.pages.Project;

import java.util.List;
import java.util.ArrayList;

/**
 * A company, agency, social club, etc.
 */
public class Organization extends ModelObject implements Resourceable {

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
     * Find or create an organization by name
     *
     * @param name String a given name
     * @return a new or existing organization
     */
    public static Organization named( String name ) {
        Dao dao = Project.getProject().getDao();
        return dao.findOrMakeOrganization( name );
    }


 }
