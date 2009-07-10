package com.mindalliance.channels.dao;

/**
 * A generator of ids.
 */
public interface IdGenerator {

    /**
     * @return the last id generated.
     */
    long getLastAssignedId();

    /**
     * Set the id counter to a new value.
     * @param id the value
     */
    void setLastAssignedId( long id );

    /**
     * Assign a new id.
     * @param id if not null, use this id and set the next assigned id accordingly.
     * @return the new id.
     */
    long assignId( Long id );

}
