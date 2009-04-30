package com.mindalliance.channels.util;

import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;

import java.text.Collator;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.CollectionUtils;

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

    private static List<String> NOISE_WORDS;

    private static final String SEPARATORS = " .,:;/\\|+-'\"()[]";

    protected SemMatch() {
    }

    static {
        String[] words = {
                "a", "an", "the", "it", "they", "we",
                "and", "or", "so", "then",
                "of", "by", "from", "at", "in", "out", "into", "off",
                "any", "all", "some", "most", "many", "few", "both",
                "for", "if", "then",
                "after", "before", "during",
                "first", "last"
        };
        NOISE_WORDS = Arrays.asList( words );
    }

    /**
     * Returns whether strings are the same (after trimming blanks and ignoring case).
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
     * Returns whether strings name the same locations.
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
     * Compares two roles for identity.
     *
     * @param role1 a Role
     * @param role2 another Role
     * @return a boolean
     */
    public static boolean sameAs( Role role1, Role role2 ) {
        return COLLATOR.compare( role1.getName(), role2.getName() ) == 0;
    }

    /**
     * Whether two string seem semantically related.
     *
     * @param string a String
     * @param other  a String
     * @return a boolean
     */
    public static boolean matches( String string, String other ) {
        // TODO - do something a wee bit smarter
        String cleanString = removeNoise( string );
        String cleanOther = removeNoise( other );
        return cleanOther.startsWith( cleanString )
                || commonWords( cleanString, cleanOther ) > 0;
    }

    private static int commonWords( String string, String other ) {
        List<String> words = Arrays.asList( StringUtils.split( string.toLowerCase(), SEPARATORS ) );
        List<String> otherWords = Arrays.asList( StringUtils.split( other.toLowerCase(), SEPARATORS ) );
        return CollectionUtils.intersection( words, otherWords ).size();
    }

    /**
     * Put string to lowercase and remove meaningless words.
     *
     * @param s a String
     * @return a String
     */
    private static String removeNoise( String s ) {
        List<String> words = new ArrayList<String>();
        words.addAll( Arrays.asList( StringUtils.split( s.toLowerCase(), SEPARATORS ) ) );
        words.removeAll( NOISE_WORDS );
        return StringUtils.join( words, ' ' );
    }
}
