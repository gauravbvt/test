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

    @Override
    public void setTemporaryIdShift( long idShift ) {
        // do nothing
    }

    @Override
    public long getIdShift() {
        return 0;
    }

    @Override
    public void cancelTemporaryIdShift() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public synchronized long getIdCounter( String uri ) {
        return lastAssignedId;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setIdCounter( long id, String uri ) {
        lastAssignedId = id;
    }

}
