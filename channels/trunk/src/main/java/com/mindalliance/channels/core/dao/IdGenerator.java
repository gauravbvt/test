package com.mindalliance.channels.core.dao;

/**
 * A generator of ids for a given URI.
 */
public interface IdGenerator {

    static final long MUTABLE_LOW = 0L;

    /**
     * @param uri a string
     * @return the last id generated.
     */
    long getIdCounter( String uri );

    /**
     * Set the id counter to a new value. Next assigned id will be that + 1.
     *
     * @param id   the value
     * @param uri a string
     */
    void setIdCounter( long id, String uri );

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

    /**
     * Shift on all assigned ids on load by given value.
     * Used temporarily on loading a model object context (plan or community).
     * @param idShift a long
     */
    void setTemporaryIdShift( long idShift );

    /**
     * Cancel temporary id shift.
     */
    void cancelTemporaryIdShift();

    /**
     * Get shifted id.
     * @param id  an id
     * @return a long
     */
    long getShiftedId( long id );
}
