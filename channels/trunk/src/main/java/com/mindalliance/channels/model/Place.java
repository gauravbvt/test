package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * A location or jurisdiction.
 */
@Entity
public class Place extends ModelObject {

    /**
     * Bogus place used to signify that the place is not known...
     */
    public static final Place UNKNOWN;

    static {
        UNKNOWN = new Place( "(unknown)" );
        UNKNOWN.setId( 10000000L - 4 );
    }

    public Place() {
    }

    public Place( String name ) {
        this();
        setName( name );
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    @Override
    public boolean isEntity() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void beforeRemove( QueryService queryService ) {
        super.beforeRemove( queryService );
        for ( Job job : queryService.findAllConfirmedJobs( ResourceSpec.with( this ) ) ) {
            job.setJurisdiction( null );
        }
        for ( Part part : queryService.findAllParts( null, ResourceSpec.with( this ) ) ) {
            part.setJurisdiction( null );
        }
        for ( Part part : queryService.findAllPartsWithLocation( this ) ) {
            part.setLocation( null );
        }
    }


}
