package com.mindalliance.channels;

import com.mindalliance.channels.nlp.Proximity;

import java.util.List;

/**
 * A semantic matcher.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 10, 2009
 * Time: 9:43:47 AM
 */
public interface SemanticMatcher {
    /**
     * Whether the semantic distance between two string is at least some given value.
     *
     * @param text      a string
     * @param otherText a string
     * @param minLevel  a proximity level
     * @return a boolean
     */
    boolean matches( String text, String otherText, Proximity minLevel );

    /**
     * Return the semantic proximity of the best match between
     * one of a list of strings and another string.
     *
     * @param strings a list of strings
     * @param string  a string
     * @return a semantic proximity value
     */
    public Proximity bestMatch( List<String> strings, String string );

    /**
     * Calculate the semantic proximity of two strings.
     *
     * @param text      a string
     * @param otherText a string
     * @return a semantic proximity value
     */
    public Proximity semanticProximity( String text, String otherText );
}
