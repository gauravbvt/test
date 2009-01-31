package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * A location or jurisdiction.
 */
@Entity
public class Place extends ModelObject {

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
        return Project.service().findOrCreate( Place.class, name );
     }

    /**
     * {@inheritDoc}
     */
    @Transient @Override
    public boolean isEntity() {
        return true;
    }

}
