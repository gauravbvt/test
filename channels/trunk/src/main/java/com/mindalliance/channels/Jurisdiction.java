package com.mindalliance.channels;

import com.mindalliance.channels.Place;

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
}
