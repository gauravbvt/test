// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * ...
 */
public class TestTag {

    private Tag tag;

    @Before
    public void init() {
        tag = new Tag( "bla" );
    }

    @Test
    public void testConstructor() {
        assertEquals( "bla", tag.getDescription() );
        assertEquals( 0L, tag.getId() );

        try {
            new Tag( (String) null );
            fail();
        } catch ( IllegalArgumentException ignored ) {
            // success
        }
    }

    @Test
    public void testEquals() {
        assertEquals( tag, tag );

        assertFalse( tag.equals( null ) );
        assertFalse( tag.equals( "bla" ) );

        Tag other = new Tag( "foo" );
        assertEquals( tag, other );
        assertEquals( other, tag );

        other.setId( 2L );
        assertFalse( tag.equals( other ) );
        assertFalse( other.equals( tag ) );

    }

    @Test
    public void testHashCode() {
        assertEquals( tag.hashCode(), new Tag().hashCode() );
    }

    @Test
    public void testToString() {
        assertEquals( "Tag[bla]", tag.toString() );
        assertEquals( "Tag[null]", new Tag().toString() );
    }

    @Test
    public void testProfiles() {
        assertNull( tag.getProfiles() );
        Set<Profile> profiles = new HashSet<Profile>();
        tag.setProfiles( profiles );
        assertSame( profiles, tag.getProfiles() );
    }

    @Test
    public void testCompareTo() {
        assertEquals( 0, tag.compareTo( tag ) );

        Tag other = new Tag( "foo" );

        assertTrue( tag.compareTo( other ) < 0 );
        assertTrue( other.compareTo( tag ) > 0 );

        Tag buggy = new Tag();
        try {
            tag.compareTo( buggy );
            fail();
        } catch ( NullPointerException ignored ) {}
        try {
            buggy.compareTo( tag );
            fail();
        } catch ( NullPointerException ignored ) {}
        try {
            buggy.compareTo( buggy );
            fail();
        } catch ( NullPointerException ignored ) {}
    }

}
