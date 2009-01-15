package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;

/**
 * A description of a jurisdiction.
 */
public class Jurisdiction extends Place {

    public Jurisdiction() {
    }

    public Jurisdiction( String name ) {
        this();
        setName( name );
    }

    /**
     * Find or create a jurisdiction by name
     *
     * @param name String a given name
     * @return a new or existing jurisdiction
     */
    public static Jurisdiction named( String name ) {
        Dao dao = Project.getProject().getDao();
        return dao.findOrMakeJurisdiction( name );
    }


}
