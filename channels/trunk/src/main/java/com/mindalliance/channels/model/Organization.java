package com.mindalliance.channels.model;

/**
 * A company, agency, social club, etc.
 */
public class Organization extends NamedObject {

    public Organization() {
    }

    /**
     * Utility constructor for tests.
     * @param name the name of the new object
     */
    public Organization( String name ) {
        super( name );
    }
}
