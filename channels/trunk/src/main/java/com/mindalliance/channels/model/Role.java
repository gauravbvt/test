package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * A generic role.
 */
@Entity
public class Role extends ModelObject {

    /**
      * Bogus role used to signify that the role is not known...
      */
     public static final Role UNKNOWN;

     static {
         UNKNOWN = new Role( "(unknown)" );
         UNKNOWN.setId( 10000000L - 5 );
     }

    public Role() {
    }

    /**
     * Utility constructor for tests.
     *
     * @param name the name of the new object
     */
    public Role( String name ) {
        super( name );
    }

    /**
     * {@inheritDoc}
     */
    @Transient @Override
    public boolean isEntity() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeRemove( QueryService queryService ) {
        super.beforeRemove( queryService );
        for ( Job job : queryService.findAllConfirmedJobs( ResourceSpec.with( this ) ) ) {
            job.setRole( null );
        }
        for ( Part part : queryService.findAllParts( null, ResourceSpec.with( this ) ) ) {
            part.setRole( null );
        }
    }


}
