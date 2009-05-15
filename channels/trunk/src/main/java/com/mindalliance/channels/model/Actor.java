package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Someone or something playing a part in a scenario.
 */
@Entity
public class Actor extends AbstractUnicastChannelable {

    /**
     * The name of the unknown actor.
     */
    public static final String UnknownName = "(unknown contact)";

    /**
     * Bogus actor used to signify that the actor is not known...
     */
    public static final Actor UNKNOWN = new Actor( UnknownName );

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
    @Override
    @Transient
    public boolean isEntity() {
        return true;
    }

    /**
     * Return a normalized version of the name.
     *
     * @return a string
     */
    public String normalize() {
        String name = getName().trim();
        if ( this == UNKNOWN || name.indexOf( ',' ) >= 0 ) return name;
        else {
            int index = name.lastIndexOf( ' ' );
            if ( index >= 0 ) {
                String s = name.substring( 0, index );
                return name.substring( index + 1 ) + ", " + s;
            } else
                return name;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void beforeRemove( QueryService queryService ) {
        super.beforeRemove( queryService );
        for ( Job job : queryService.findAllConfirmedJobs( ResourceSpec.with( this ) ) ) {
            job.setActor( null );
        }
        for ( Part part : queryService.findAllPartsWith( ResourceSpec.with( this ) ) ) {
            part.setActor( null );
        }
    }

}
