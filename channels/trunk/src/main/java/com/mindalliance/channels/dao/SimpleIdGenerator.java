package com.mindalliance.channels.dao;

import com.mindalliance.channels.model.Plan;

/**
 * a simple implementation of an id generator.
 */
public class SimpleIdGenerator implements IdGenerator {

    /** The last assigned id. */
    private long lastAssignedId;

    public SimpleIdGenerator() {
    }

    /**
     * {@inheritDoc}
     */
    public synchronized long assignId( Long id, Plan plan ) {
        if ( id == null )
            return lastAssignedId++;
        else {
            lastAssignedId = Math.max( lastAssignedId, id );
            return id;
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized long getLastAssignedId( Plan plan ) {
        return lastAssignedId;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setLastAssignedId( long id, Plan plan ) {
        lastAssignedId = id;
    }

}
