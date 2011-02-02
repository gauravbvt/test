package com.mindalliance.channels.model;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A tag.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/27/11
 * Time: 1:11 PM
 */
public class Tag implements Serializable, Nameable, Comparable {
    /**
     * Tag composition string
     */
    public static final String COMPOSITOR = ":";
    /**
     * Tag separator
     */
    public static final String SEPARATOR = ",";

    private static Collator collator = Collator.getInstance();

    private String name;

    public Tag( String s ) {
        name = StringUtils.strip( s.trim(), COMPOSITOR ).trim();
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public List<String> getElements() {
        return getName().isEmpty() ? new ArrayList<String>() :
                Arrays.asList( getName().split( COMPOSITOR ) );
    }

    public boolean isComposed() {
        return getName().indexOf( COMPOSITOR ) > 0;
    }

    @Override
    public boolean equals( Object other ) {
        return other instanceof Tag
                && getName().toLowerCase().equals( ( (Tag) other ).getName().toLowerCase() );
    }

    @Override
    public int hashCode() {
        return 31 * getName().toLowerCase().hashCode();
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
                if ( !tags.contains( tag ) ) tags.add( tag );
            }
        }
        return tags;
    }

    public List<String> getAllComponents() {
        List<String> components = new ArrayList<String>();
        List<String> elements = getElements();
        StringBuilder sb = new StringBuilder();
        for ( String element : elements ) {
            if ( sb.length() > 0 ) sb.append( COMPOSITOR );
            sb.append( element );
            components.add( sb.toString() );
        }
        return components;
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean isEmpty() {
        return getName().isEmpty();
    }

    @Override
    public int compareTo( Object other ) {
        return collator.compare( getName(), ((Tag)other).getName() );
    }
}
