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
      * @return a new or existing place, or null is name is null or empty
      */
     public static Place named( String name ) {
        if ( name == null || name.isEmpty() ) return null;
         Dao dao = Project.getProject().getDao();
         return dao.findOrMakePlace( name );
     }

}
