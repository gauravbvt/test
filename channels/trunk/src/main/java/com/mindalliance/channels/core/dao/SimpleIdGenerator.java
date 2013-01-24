package com.mindalliance.channels.core.dao;

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
    public synchronized long assignId( Long id, String uri ) {
        if ( id == null )
            return lastAssignedId++;
        else {
            lastAssignedId = Math.max( lastAssignedId, id );
            return id;
        }
    }

    @Override
    public void setImmutableMode() {
        // do nothing
    }

    @Override
    public void setMutableMode() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public synchronized long getLastAssignedId( String uri ) {
        return lastAssignedId;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setLastAssignedId( long id, String uri ) {
        lastAssignedId = id;
    }

}
