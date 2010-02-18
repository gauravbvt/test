// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.dao;

import com.mindalliance.mindpeer.model.Tag;

import java.util.Collection;
import java.util.Set;
import java.util.List;

/**
 * Accessor for tags.
 */
public interface TagDao {

    /**
     * Get or create a persistent tag from a string description.
     *
     * @param description the given description
     * @return the corresponding tag
     */
    Tag get( String description );

    /**
     * Get a tag given its id.
     *
     * @param id the given id
     * @return the corresponding tag or null
     */
    Tag get( long id );

    /**
     * Convert a collection of tag descriptions into a set of persistent tags.
     *
     * @param descriptions the given descriptions
     * @return a set of corresponding tag
     */
    Set<Tag> get( Collection<String> descriptions );

    /**
     * Delete a tag.
     * @param tag the tag to delete. May raise an exception when used somewhere else.
     */
    void delete( Tag tag );

    /**
     * Return all defined tags.
     * @return list of tags
     */
    List<Tag> getAll();

    /**
     * Count all tags defined in the system.
     * @return the number of tags
     */
    int countAll();
}
