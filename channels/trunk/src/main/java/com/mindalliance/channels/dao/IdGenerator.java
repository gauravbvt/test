package com.mindalliance.channels.dao;

import com.mindalliance.channels.model.Plan;

/**
 * A generator of ids.
 */
public interface IdGenerator {

    /**
     * @param plan a plan
     * @return the last id generated.
     */
    long getLastAssignedId( Plan plan );

    /**
     * Set the id counter to a new value.
     *
     * @param id   the value
     * @param plan a plan
     */
    void setLastAssignedId( long id, Plan plan );

    /**
     * Assign a new id.
     *
     * @param id   if not null, use this id and set the next assigned id accordingly.
     * @param plan a plan
     * @return the new id.
     */
    long assignId( Long id, Plan plan );

}
