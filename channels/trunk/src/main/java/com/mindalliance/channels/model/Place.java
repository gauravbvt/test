package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * A location or jurisdiction.
 */
@Entity
public class Place extends ModelObject {

    public Place() {
    }

    public Place( String name ) {
        this();
        setName( name );
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
           job.setJurisdiction( null );
       }
       for (Part part : queryService.findAllPartsWith( ResourceSpec.with( this ) )) {
           part.setJurisdiction( null );
       }
        for (Part part : queryService.findAllPartsWithLocation( this )) {
            part.setLocation( null );
        }
    }



}
