package com.mindalliance.channels.core.dao;

/**
 * A generator of ids for a given URI.
 */
public interface IdGenerator {

    /**
     * @param uri a string
     * @return the last id generated.
     */
    long getLastAssignedId( String uri );

    /**
     * Set the id counter to a new value.
     *
     * @param id   the value
     * @param uri a string
     */
    void setLastAssignedId( long id, String uri );

    /**
     * Assign a new id.
     *
     * @param id   if not null, use this id and set the next assigned id accordingly.
     * @param uri a string
     * @return the new id.
     */
    long assignId( Long id, String uri );

    /**
     * Use id range for immutable objects.
     */
    void setImmutableMode();

    /**
     * Use id range for mutable object (default)
     */
    void setMutableMode();
}
