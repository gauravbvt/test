package com.mindalliance.channels.core.model;

import org.apache.commons.lang.StringUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A tag.
 */
public class Tag implements Nameable, Comparable<Tag> {

    /** Tag composition string */
    public static final String COMPOSITOR = ":";

    /** Tag separator */
    public static final String SEPARATOR = ",";

    public static final String VISIBILITY_SUFFIX = "*";

    private static final Collator CollatorInstance = Collator.getInstance();

    private final String name;

    public Tag( String s ) {
        name = StringUtils.strip( s.trim(), COMPOSITOR ).trim();
    }

    @Override
    public String getName() {
        return name;
    }

    public List<String> getElements() {
        return name.isEmpty() ? new ArrayList<String>() : Arrays.asList( name.split( COMPOSITOR ) );
    }

    public boolean isComposed() {
        return name.indexOf( COMPOSITOR ) > 0;
    }

    @Override
    public boolean equals( Object obj ) {
        return obj instanceof Tag && name.equalsIgnoreCase( ( (Tag) obj ).getName() );
    }

    @Override
    public int hashCode() {
        return 31 * name.toLowerCase().hashCode();
    }

    public static String tagsToString( List<Tag> tags ) {
        StringBuilder sb = new StringBuilder();
        Iterator<Tag> iter = tags.iterator();
        while ( iter.hasNext() ) {
            sb.append( iter.next().getName() );
            if ( iter.hasNext() ) {
                sb.append( SEPARATOR );
                sb.append( ' ' );
            }
        }
        return sb.toString();
    }

    public static List<Tag> tagsFromString( String s ) {
        List<Tag> tags = new ArrayList<Tag>();
        for ( String val : s.split( SEPARATOR ) ) {
            String name = val.trim();
            if ( !name.isEmpty() ) {
                Tag tag = new Tag( val );
                if ( !tags.contains( tag ) )
                    tags.add( tag );
            }
        }
        return tags;
    }

    public List<String> getAllComponents() {
        List<String> components = new ArrayList<String>();
        List<String> elements = getElements();
        StringBuilder sb = new StringBuilder();
        for ( String element : elements ) {
            if ( sb.length() > 0 )
                sb.append( COMPOSITOR );
            sb.append( element );
            components.add( sb.toString() );
        }
        return components;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isEmpty() {
        return name.isEmpty();
    }

    @Override
    public int compareTo( Tag other ) {
        return CollatorInstance.compare( name, other.getName() );
    }

    public static List<Tag> copy( List<Tag> tags ) {
        return tagsFromString( tagsToString( tags) );
    }

    public boolean isVisible() {
        return name.endsWith( VISIBILITY_SUFFIX );
    }

    public Tag normalize() {
        return isVisible()
                ? new Tag( stripVisibilitySuffix() )
                : this;
    }

    private String stripVisibilitySuffix() {
        int i = name.lastIndexOf( VISIBILITY_SUFFIX );
        if ( i > 0 ) {
            return name.substring( 0, i );
        } else {
            return name;
        }
    }
}
