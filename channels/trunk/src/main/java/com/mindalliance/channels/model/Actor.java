package com.mindalliance.channels.model;

/**
 * Someone or something playing a part in a scenario.
 */
public abstract class Actor extends Node {

    protected Actor() {
    }

    /**
     * Utility constructor for tests.
     * @param name the name of the new object
     */
    protected Actor( String name ) {
        super( name );
    }
    
}
