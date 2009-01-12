package com.mindalliance.channels.util;

import com.mindalliance.channels.Jurisdiction;

/**
 * A matching utility
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 18, 2008
 * Time: 9:55:28 PM
 */
public class SemMatch {

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
        return string.trim().equalsIgnoreCase( otherString.trim() );
    }

    /**
     * Returns whether strings name the same locations
     *
     * @param loc      -- a string
     * @param otherLoc -- another string
     * @return -- whether they are similar
     */
    public static boolean sameLocation( Jurisdiction loc, Jurisdiction otherLoc ) {
        // TODO - compare geofeatures
        if (loc == null || otherLoc == null)
            return false;
        else
            return same( loc.getName(), otherLoc.getName() );
    }
}
