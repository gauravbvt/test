package com.mindalliance.channels.nlp;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A matching utility.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 18, 2008
 * Time: 9:55:28 PM
 */
public class Matcher {

    public static List<String> NOISE_WORDS;

    /**
     * A comparator
     */
    private static final Collator COLLATOR = Collator.getInstance();

    private static final String SEPARATORS = " .,:;?!/\\|+-'\"()[]";

    private static final Matcher instance = new Matcher();

    static {
        String[] words = {
            "a", "an", "the", "it", "they", "we",
            "and", "or", "so", "then",
            "of", "by", "from", "at", "in", "out", "into", "off", "to", "on",
            "any", "all", "some", "most", "many", "few", "both",
            "for", "if", "then",
            "after", "before", "during",
            "first", "last",
            "I", "you", "they", "us", "your", "my", "them"
        };
        NOISE_WORDS = Arrays.asList( words );
    }

    public Matcher() {
    }

    public static Matcher getInstance() {
        return instance;
    }

    /**
     * Returns whether strings are non-empty and equivalent (after trimming blanks and ignoring case).
     *
     * @param string      -- a string
     * @param otherString -- another string
     * @return -- whether they are similar
     */
    public boolean same( String string, String otherString ) {
        String trimmed = string.trim();
        String otherTrimmed = otherString.trim();
        return !trimmed.isEmpty()
                && !otherTrimmed.isEmpty()
                && COLLATOR.compare( trimmed.toLowerCase(), otherTrimmed.toLowerCase() ) == 0;
    }

    /**
     * Whether two string seem semantically related.
     *
     * @param string a String
     * @param other  a String
     * @return a boolean
     */
    public boolean matches( String string, String other ) {
        if ( string.isEmpty() || other.isEmpty() ) return false;
        // TODO - maybe do something a wee bit smarter
        String cleanString = removeNoise( string );
        String cleanOther = removeNoise( other );
        return cleanOther.startsWith( cleanString )
                || commonWords( cleanString, cleanOther ) > 0;
    }

    private int commonWords( String string, String other ) {
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
    private String removeNoise( String s ) {
        List<String> words = new ArrayList<String>();
        words.addAll( Arrays.asList( StringUtils.split( s.toLowerCase(), SEPARATORS ) ) );
        words.removeAll( NOISE_WORDS );
        return StringUtils.join( words, ' ' );
    }

    /**
     * Extract EOI strings from free-form text.
     *
     * @param text a string with EOIs separated by '\n' or '.'
     * @return a list of strings
     */
    public List<String> extractEOIs( String text ) {
        List<String> eois = new ArrayList<String>();
        for ( String s : StringUtils.split( text, "\n" ) ) {
            String eoi = s.trim();
            if ( !eoi.isEmpty() ) eois.add( eoi );
        }
        return eois;
    }
}
