package com.mindalliance.channels.model;

/**
 * A physical location.
 */
public class Location extends Place {

    public Location() {
    }

    public Location( String name ) {
        this();
        setName( name );
    }
}
