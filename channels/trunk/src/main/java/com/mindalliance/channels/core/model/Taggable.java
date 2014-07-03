package com.mindalliance.channels.core.model;

import java.util.List;

/**
 * That which has tags.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/27/11
 * Time: 1:20 PM
 */
public interface Taggable extends Identifiable {
    /**
     * Get tags.
     *
     * @return a list of normalized tags
     */
    List<Tag> getTags();

    /**
     * Get all tagged marked for visibility in labels normalized
     * @return a list of tags
     */
    List<Tag> getVisibleTags();

    /**
     * Get all tags as-is (with visibility suffixes if any)
     * @return a list of tags
     */
    List<Tag> getRawTags();

    /**
     * Set tags from string.
     *
     * @param s a string
     */
    void setTagsAsString( String s );

    /**
     * Add tags from string.
     *
     * @param s a string
     */
    void addTags( String s );

    /**
     * Add a tag.
     *
     * @param tag a tag
     */
    void addTag( Tag tag );

}
