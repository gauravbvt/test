package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;

/**
 * A location or jurisdiction.
 */
public class Place extends ModelObject implements Entity {

    public Place() {
    }

    public Place( String name ) {
        this();
        setName( name );
    }

    /**
      * Find or create a place by name
      *
      * @param name String a given name
      * @return a new or existing place
      */
     public static Place named( String name ) {
         Dao dao = Project.getProject().getDao();
         return dao.findOrMakePlace( name );
     }

}
