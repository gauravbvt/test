package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * A generic role.
 */
@Entity
public class Role extends ModelObject {

    /** The undefined role. */
    public static final Role UNKNOWN = new Role( "(Unknown role)" );

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
    public void beforeRemove( QueryService queryService ) {
        for ( Job job : queryService.findAllConfirmedJobs( ResourceSpec.with( this ) ) ) {
           job.setRole( null );
       }
       for (Part part : queryService.findAllPartsWith( ResourceSpec.with( this ) )) {
           part.setRole( null );
       }
    }


}
