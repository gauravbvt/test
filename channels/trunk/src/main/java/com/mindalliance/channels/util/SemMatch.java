package com.mindalliance.channels.util;

import com.mindalliance.channels.Place;
import com.mindalliance.channels.Role;

import java.text.Collator;

/**
 * A matching utility
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 18, 2008
 * Time: 9:55:28 PM
 */
public class SemMatch {
    /**
     * A comparator
     */
    private static final Collator COLLATOR = Collator.getInstance();

    protected SemMatch() {
    }

    /**
     * Returns whether strings are the same (after trimming blanks and ignoring case)
     *
     * @param string      -- a string
     * @param otherString -- another string
     * @return -- whether they are similar
     */
    public static boolean same( String string, String otherString ) {
        return COLLATOR.compare(
                string.toLowerCase().trim(),
                otherString.toLowerCase().trim() ) == 0;
    }

    /**
     * Returns whether strings name the same locations
     *
     * @param place -- a string
     * @param other -- another string
     * @return -- whether they are similar
     */
    public static boolean samePlace( Place place, Place other ) {
        if ( place == null && other == null )
            return true;
        if ( place == null || other == null )
            return false;
        // TODO - compare geofeatures
        return same( place.getName(), other.getName() );
    }

    /**
     * Compares two roles for identity
     * @param role1 a Role
     * @param role2 another Role
     * @return a boolean
     */
    public static boolean sameAs( Role role1, Role role2 ) {
        return COLLATOR.compare( role1.getName(), role2.getName() ) == 0;
    }
}
