package com.mindalliance.channels.core;

import com.mindalliance.channels.core.model.Tag;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A matching utility.
 */
public final class Matcher {

    /**
     * Words that should be ignored when matching.
     */
    private static final List<String> NOISE_WORDS = Arrays.asList( "a", "an", "the", "it", "they", "we", "and", "or",
            "so", "then", "of", "by", "from", "at", "in", "out",
            "into", "off", "to", "on", "any", "all", "some",
            "most", "many", "few", "both", "for", "if", "then",
            "after", "before", "during", "first", "last", "I",
            "you", "they", "us", "your", "my", "them" );

    /**
     * A comparator.
     */
    private static final Collator COLLATOR = Collator.getInstance();

    private static final String SEPARATORS = " .,:;?!/\\|+-'\"()[]@";

    //-------------------------------

    /**
     * Don't allow instantiations.
     */
    private Matcher() {
    }

    /**
     * Whether any in a list of strings matches a given string.
     *
     * @param list a list of strings
     * @param s    a string
     * @return a boolean
     */
    public static boolean contains( List<String> list, final String s ) {
        return CollectionUtils.exists( list, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return same( s, (String) object );
            }
        } );
    }

    /**
     * Return canonical form of a string.
     *
     * @param s a string
     * @return a string
     */
    public static String makeCanonical( String s ) {
        return s.trim().toLowerCase();
    }

    /**
     * Whether two string seem semantically related.
     *
     * @param string a String
     * @param other  a String
     * @return a boolean
     */
    public static boolean matches( String string, String other ) {
        if ( string.isEmpty() || other.isEmpty() )
            return false;

        // TODO - maybe do something a wee bit smarter
        String cleanString = removeNoise( string );
        String cleanOther = removeNoise( other );
        return cleanOther.startsWith( cleanString ) || commonWords( cleanString, cleanOther ) > 0;
    }

    /**
     * Put string to lowercase and remove meaningless words.
     *
     * @param s a String
     * @return a String
     */
    private static String removeNoise( String s ) {
        List<String> words = new ArrayList<String>();
        words.addAll( Arrays.asList( StringUtils.split( makeCanonical( s ), SEPARATORS ) ) );
        words.removeAll( NOISE_WORDS );
        return StringUtils.join( words, ' ' );
    }

    private static int commonWords( String string, String other ) {
        List<String> words = Arrays.asList( StringUtils.split( makeCanonical( string ), SEPARATORS ) );
        List<String> otherWords = Arrays.asList( StringUtils.split( makeCanonical( other ), SEPARATORS ) );
        return CollectionUtils.intersection( words, otherWords ).size();
    }

    /**
     * Whether the string contains all of the matching strings contains (case insensitive).
     *
     * @param string       a string
     * @param otherStrings a list of strings
     * @return a boolean
     */
    public static boolean matchesAll( String string, List<String> otherStrings ) {
        String slc = string.toLowerCase();
        for ( String o : otherStrings ) {
            if ( !slc.contains( o.toLowerCase() ) )
                return false;
        }
        return true;
    }

    /**
     * Remove from list all matching strings from other list.
     *
     * @param list     list of strings
     * @param toRemove list of strings to remove
     */
    public static void removeAll( Set<String> list, List<String> toRemove ) {
        for ( String s : toRemove )
            for ( String item : new ArrayList<String>( list ) )
                if ( same( s, item ) )
                    list.remove( item );
    }

    /**
     * Returns whether strings are non-empty and equivalent (after trimming blanks and ignoring case).
     *
     * @param string      -- a string
     * @param otherString -- another string
     * @return -- whether they are similar
     */
    public static boolean same( String string, String otherString ) {
        String trimmed = makeCanonical( string );
        String otherTrimmed = makeCanonical( otherString );
        return !trimmed.isEmpty() && !otherTrimmed.isEmpty() && COLLATOR.compare( trimmed, otherTrimmed ) == 0;
    }

    /**
     * Returns whether strings are empty or equivalent (after trimming blanks and ignoring case).
     *
     * @param string      -- a string
     * @param otherString -- another string
     * @return -- whether they are similar
     */
    public static boolean sameOrEmpty( String string, String otherString ) {
        return string.isEmpty() && otherString.isEmpty() || same( string, otherString );
    }


    /**
     * Return whether two possible composed tags match.
     *
     * @param tag   a tag
     * @param other a tag
     * @return a boolean
     */
    public static boolean matches( Tag tag, Tag other ) {
        if ( tag.equals( other ) ) return true;
        List<String> elements = tag.getElements();
        List<String> otherElements = other.getElements();
        Iterator<String> shorter = elements.size() < otherElements.size()
                ? elements.iterator()
                : otherElements.iterator();
        Iterator<String> longer = elements.size() >= otherElements.size()
                ? elements.iterator()
                : otherElements.iterator();
        boolean similar = true;
        while ( similar && shorter.hasNext() ) {
            similar = Matcher.matches( shorter.next(), longer.next() );
        }
        return similar;
    }

    /**
     * Return whether all given tags are matched by other tags.
     *
     * @param tags   a list of tags
     * @param others a list of tags
     * @return a boolean
     */
    public static boolean matchesAll( List<Tag> tags, final List<Tag> others ) {
        // There is no tag not matched by some other.
        return !CollectionUtils.exists(
                tags,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final Tag tag = (Tag) object;
                        return !CollectionUtils.exists(
                                others,
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        return matches( tag, (Tag) object );
                                    }
                                }
                        );
                    }
                }
        );
    }

    /**
     * Whether a given text contains a given phrase, irrespective of case or punctuation etc.
     *
     * @param text   a string
     * @param phrase a string
     * @return a boolean
     */
    public static boolean contains( String text, String phrase ) {
        return StringUtils.contains( removeNoise( text ), removeNoise( phrase ) );
    }
}
