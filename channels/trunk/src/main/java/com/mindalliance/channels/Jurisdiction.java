package com.mindalliance.channels;

/**
 * A description of a jurisdiction.
 */
public class Jurisdiction extends Place  implements Resourceable {

    public Jurisdiction() {
    }

    public Jurisdiction( String name ) {
        this();
        setName( name );
    }

}
