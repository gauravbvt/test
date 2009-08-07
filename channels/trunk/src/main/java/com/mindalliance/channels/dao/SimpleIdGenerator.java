package com.mindalliance.channels.dao;

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
    public synchronized long assignId( Long id ) {
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
    public synchronized long getLastAssignedId() {
        return lastAssignedId;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setLastAssignedId( long id ) {
        lastAssignedId = id;
    }
}
