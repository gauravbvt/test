package com.mindalliance.channels;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Someone or something playing a part in a scenario.
 */
@Entity
public class Actor extends AbstractUnicastChannelable {

    /** Bogus actor used to signify that the actor is not known... */
    public static final Actor UNKNOWN = new Actor( "(unknown contact)" );

    public Actor() {
    }

    /**
     * Utility constructor for tests.
     *
     * @param name the name of the new object
     */
    public Actor( String name ) {
        super( name );
    }

    /**
     * {@inheritDoc}
     */
    @Override @Transient
    public boolean isEntity() {
        return true;
    }

    /**
     * Return a normalized version of the name.
     * @return a string
     */
    public String normalize() {
        String name = getName().trim();
        if (this == UNKNOWN || name.indexOf( ',') >= 0 ) return name;
        else {
           int index = name.lastIndexOf( ' ' );
            if (index >= 0 ) {
                String s = name.substring( 0, index );
                return name.substring( index + 1 ) + ", " + s;
            }
            else
                return name;
        }
    }

}
