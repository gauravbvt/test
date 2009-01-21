package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;

/**
 * A company, agency, social club, etc.
 */
public class Organization extends ModelObject implements Entity {

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
     * @return a new or existing organization, or null is name is null or empty
     */
    public static Organization named( String name ) {
        if ( name == null || name.isEmpty() ) return null;
        Dao dao = Project.getProject().getDao();
        return dao.findOrMakeOrganization( name );
    }


 }
