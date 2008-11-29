package com.mindalliance.channels;

import com.mindalliance.channels.ModelObject;

/**
 * A company, agency, social club, etc.
 */
public class Organization extends ModelObject {

    public Organization() {
    }

    // TODO Add properties: mission, parent

    /**
     * Utility constructor for tests.
     * @param name the name of the new object
     */
    public Organization( String name ) {
        super( name );
    }
}
