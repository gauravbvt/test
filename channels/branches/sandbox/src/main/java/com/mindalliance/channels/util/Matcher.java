package com.mindalliance.channels.util;

import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.nlp.Proximity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A matching utility
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 18, 2008
 * Time: 9:55:28 PM
 */
public class Matcher {
    /**
     * A comparator
     */
    private static final Collator COLLATOR = Collator.getInstance();

    public static List<String> NOISE_WORDS;

    private static final String SEPARATORS = " .,:;?!/\\|+-'\"()[]";

    protected Matcher() {
    }

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

    /**
     * Returns whether strings are non-empty and equivalent (after trimming blanks and ignoring case).
     *
     * @param string      -- a string
     * @param otherString -- another string
     * @return -- whether they are similar
     */
    public static boolean same( String string, String otherString ) {
        String trimmed = string.trim();
        String otherTrimmed = otherString.trim();
        return !trimmed.isEmpty()
                && !otherTrimmed.isEmpty()
                && COLLATOR.compare( trimmed.toLowerCase(), otherTrimmed.toLowerCase() ) == 0;
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
        if ( string.isEmpty() || other.isEmpty() ) return false;
        // TODO - maybe do something a wee bit smarter
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

    /**
     * Whether a place's geolocation is the same or within another's.
     *
     * @param place a containing place
     * @param other a contained place
     * @return a boolean
     */
    public static boolean within( Place place, Place other ) {
        return !( place == null || other == null ) && place.matchesOrIsInside( other );
    }

    /**
     * Whether there are common EOIs in two free-form texts.
     *
     * @param flow         a flow
     * @param otherFlow    a flow
     * @param queryService a query service
     * @return a boolean
     */
    public static boolean hasCommonEOIs( Flow flow, Flow otherFlow, final QueryService queryService ) {
        List<ElementOfInformation> eois = flow.getEois();
        final List<ElementOfInformation> otherEois = otherFlow.getEois();
        return CollectionUtils.exists(
                eois,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        final String eoi = ( (ElementOfInformation) obj ).getContent();
                        return CollectionUtils.exists(
                                otherEois,
                                new Predicate() {
                                    public boolean evaluate( Object o ) {
                                        String otherEoi = ( (ElementOfInformation) o ).getContent();
                                        return queryService.isSemanticMatch( eoi, otherEoi, Proximity.HIGH );
                                    }
                                } );
                    }
                } );
    }

    /**
     * Extract EOI strings from free-form text.
     *
     * @param text a string with EOIs separated by '\n' or '.'
     * @return a list of strings
     */
    public static List<String> extractEOIs( String text ) {
        List<String> eois = new ArrayList<String>();
        for ( String s : StringUtils.split( text, "\n" ) ) {
            String eoi = s.trim();
            if ( !eoi.isEmpty() ) eois.add( eoi );
        }
        return eois;
    }

    /**
     * Whether none in a list eois is without a strong match with some in another list.
     *
     * @param eois         a list of elements of information
     * @param superset     a list of elements of information
     * @param queryService a query service
     * @return a boolean
     */
    public static boolean subsetOf(
            List<ElementOfInformation> eois,
            final List<ElementOfInformation> superset,
            final QueryService queryService ) {
        return !CollectionUtils.exists(
                eois,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        final String eoi = ( (ElementOfInformation) obj ).getContent();
                        return !CollectionUtils.exists(
                                superset,
                                new Predicate() {
                                    public boolean evaluate( Object o ) {
                                        final String otherEoi = ( (ElementOfInformation) o ).getContent();
                                        return queryService.isSemanticMatch( eoi, otherEoi, Proximity.HIGH );
                                    }
                                }
                        );
                    }
                }
        );
    }
}
